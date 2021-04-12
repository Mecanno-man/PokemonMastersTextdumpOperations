# Pokémon Masters Textdump Operations

This project contains a varity of operations dealing with Pokémon Masters EX textdumps used for [PokéWiki](https://www.pokewiki.de/). Precisely, when run it will create:
* a textdump of German language files only. (`dumpGerman.txt`)
* a changelog for German language files. (`changeLog.txt`)
* a PokéWiki dictionary from German move names to their German description. (`moveMap.txt`)
* a PokéWiki dictionary from German passive skill names to their German description. (`passiveSkillMap.txt`)
* a folder containing the German Pokémon Center quotes of all characters in the format used on PokéWiki, though only configured properly for sync pairs.(`/characterPokémonCenterQuotes`)

## Usage

Compile java files and run Driver to run.

Input text dump needs to be named `newDump.txt`. Additionally, for the change log an old text dump named `oldDump.txt` also needs to be present.

Text dumps can be acquired from [abcboy's user page on Bulbapedia](https://bulbapedia.bulbagarden.net/wiki/User:Abcboy). The required file from abcboy's dump is `lsd_dl.txt` or `lsd_dl (2).txt`. This is the only format supported; note that some older dumps on abcboy's page don't conform to the later format. These do not work with the program.