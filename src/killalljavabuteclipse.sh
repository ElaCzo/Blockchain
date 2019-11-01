#!/bin/bash

jps | egrep -v "eclipse" | egrep -v "Jps" | cut -b1-5 | xargs -t kill
ps | grep "scrabblos-serve" | cut -b1-6 | xargs -t kill
