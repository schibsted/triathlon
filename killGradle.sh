#!/bin/bash
ps aux | grep gradle | grep $USER | grep Triathlon | awk '{print $2}' | xargs kill -9
