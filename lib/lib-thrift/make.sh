#!/bin/bash

cd `dirname $0` && sh ./idls/gen.sh && mvn clean install
