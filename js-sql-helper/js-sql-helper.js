const crypto = require("crypto");
const md5 = require("md5");

const key = "my secret key";
const iv = "my initialisation vector";

// SQL helper functions

sqle = (text) => {
    // returns an sql command string to encrypt the given string
    return ' AES_ENCRYPT("' + text + '", "' + key + '")';
}

sqld = (col, as = true) => {
    // returns an sql command string to decrypt the given encrypted column
    // as set implies add "AS col" to the end
    return ' CAST(AES_DECRYPT(' + col + ', "' + key + '") AS CHAR) ' + (as ? 'AS ' + col + ' ' : "");
}

sqlmd5 = (text, withQuotes = true) => {
    // returns an md5 of the text with or without quotes
    return (withQuotes ? '"' : "") + md5(text) + (withQuotes ? '"' : "");
}

sqlc = () => {
    // returns a comma string
    return ', ';
}

sqlq = (text = null) => {
    // wraps the given text in quotes
    if(!text) return '"';
    else return '"' + text + '"';
}

sqll = (col, text) => {
    // returns the sql LIKE string for "col" with wildcards either side of "text"
    return ' ' + col + ' LIKE "%' + text + '%" ';
}

sqlld = (col, text) => {
    // same as sqll but decrypts col first
    // instead of using sqlld you could just use HAVING instead of WHERE with sqll but HAVING is slower !
    return ' ' + sqld(col, false) + ' LIKE "%' + text + '%" ';
}

sqlor = () => {
    // returns OR
    return " OR ";
}

sqland = () => {
    // returns AND
    return " AND ";
}

sqldq = (text) => {
    // replaces all quotes with escaped quotes
    return text.replace(/"/g, '\\"');
}

// bonus encryption/decryption wrappers

b64e = (text) => {
    // returns a base64 encoding of text
    return Buffer.from(text).toString("base64");
}

b64d = (text) => {
    // returns the ascii decode of the base64 text
    return Buffer.from(text, "base64").toString("ascii");
}

function encrypt(data) {
    // encrypts
    const algorithm = "aes-256-cbc";
    let cipher = crypto.createCipheriv(algorithm, Buffer.from(key, "hex"), Buffer.from(iv, "hex"));
    let encrypted = cipher.update(JSON.stringify(data));
    encrypted = Buffer.concat([encrypted, cipher.final()]);
    return encrypted.toString("hex");
}

function decrypt(encoded) {
    // decrypts
    const algorithm = "aes-256-cbc";
    let encryptedText = Buffer.from(encoded, "hex");
    let decipher = crypto.createDecipheriv(algorithm, Buffer.from(key, "hex"), Buffer.from(iv, "hex"));
    let decrypted = decipher.update(encryptedText);
    decrypted = Buffer.concat([decrypted, decipher.final()]);
    return JSON.parse(decrypted.toString());
}

function verifyJWT(jwt) {
  // decodes the given jwt, or returns null on fail or expiry
  try {
    let info = decrypt(jwt);
    return info.expires < Date.now() ? info : null;
  } catch {
    return null;
  }
}
