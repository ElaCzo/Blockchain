#!/bin/bash

jps | egrep -v "eclipse" | egrep -v "Jps" | cut -b1-6 | xargs -t kill
