# YOMI
read japanese kanji

Yomi uses some dictionary files in c:\YOMI

edict.gz

enamdict.gz

Perhaps it can be found at http://www.edrdg.org/jmdict/edict.html

Then server runs, and the user js script makes requests.
F2 to enable
Highlight to show a box with the reading.


4/19/2017

Problems:

kanji with multiple readings are prone to error.
Any next steps may be a HUGE increase in technical complexity, e.g. creating grammar rules, or exceptions.

Browser Script needs to not add the extra div before enabling because it may cause unnecessary spawn inside every iframe.
And it is known to affect fogbugz html editor by inserting the garbage div lol
