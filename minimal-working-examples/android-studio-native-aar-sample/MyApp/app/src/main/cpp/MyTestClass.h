//
// Created by Paul on 22/08/2021.
//

#ifndef MYAPP_MYTESTCLASS_H
#define MYAPP_MYTESTCLASS_H

#include "MyLibraryCPP.h"

class MyTestClass {
    MyLibraryCPP* x = new MyLibraryCPP();

    MyTestClass() {
        x->foo();
    }

    ~MyTestClass() {
        delete x;
    }
};

#endif //MYAPP_MYTESTCLASS_H
