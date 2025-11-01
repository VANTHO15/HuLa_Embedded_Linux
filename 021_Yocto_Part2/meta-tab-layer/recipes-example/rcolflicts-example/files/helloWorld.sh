#!/bin/bash

isPresent="$(which echostatus)"

if [[ -n $isPresent ]]; then
    echostatus i -b "This is Info Message"
    echostatus w -b "This is Warning Message"
    echostatus e -b "This is Error Message"
    echostatus s -b "This is Success Message"
else
    echo "This is Info Message"
    echo "This is Warning Message"
    echo "This is Error Message"
    echo "This is Success Message"
fi
