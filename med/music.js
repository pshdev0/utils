console.log("poo");

let midi = null;  // global MIDIAccess object
function onMIDISuccess( midiAccess ) {
  console.log( "MIDI ready!" );
  midi = midiAccess;  // store in the global (in real usage, would 
probably keep in an object instance)

  listInputsAndOutputs(midiAccess);
}

function onMIDIFailure(msg) {
  console.log( "Failed to get MIDI access - " + msg );
}

console.log("requesting MIDI");
navigator.requestMIDIAccess().then( onMIDISuccess, onMIDIFailure );




// ----


function listInputsAndOutputs( midiAccess ) {
  for (const entry of midiAccess.inputs) {
    const input = entry[1];
    console.log( "Input port [type:'" + input.type + "'] id:'" + input.id +
      "' manufacturer:'" + input.manufacturer + "' name:'" + input.name +
      "' version:'" + input.version + "'" );
  }

  for (const entry of midiAccess.outputs) {
    const output = entry[1];
    console.log( "Output port [type:'" + output.type + "'] id:'" + output.id +
      "' manufacturer:'" + output.manufacturer + "' name:'" + output.name +
      "' version:'" + output.version + "'" );
  }
}




