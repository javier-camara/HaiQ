#!/usr/bin/python

import subprocess, sys

commands = [
    ['pdflatex', '-shell-escape', sys.argv[1] + '.tex'],
    ['bibtex', sys.argv[1] + '.aux'],
    ['pdflatex', '-shell-escape', sys.argv[1] + '.tex'],
    ['pdflatex', '-shell-escape', sys.argv[1] + '.tex'],
    ['open', '-a', 'Preview.app', sys.argv[1] + '.pdf']
]

for c in commands:
    subprocess.call(c)