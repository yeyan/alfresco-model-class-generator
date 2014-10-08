#!/usr/bin/env python

import os, sys

scriptHome = os.path.dirname(os.path.realpath(__file__))
jarPath = "%s/%s" % (scriptHome, "model-gen-0.1.0.jar")

def quoted_args():
    for argv in sys.argv[1:]:
        yield "\"" + argv + "\""

os.system("java -jar %s %s" % (jarPath, " ".join(quoted_args())))
