#!/bin/bash

echo '$0: '$0
echo "pwd: "`pwd`
echo "scriptPath1: "$(cd `dirname $0`; pwd)
