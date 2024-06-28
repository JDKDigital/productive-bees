# Productive Bees

<a href="https://www.curseforge.com/minecraft/mc-mods/productivebees/files"><img src="https://img.shields.io/badge/Available%20for-MC%201.15.2,%201.16.5,%201.18.2,%201.19.2,%201.20.1-c70039" alt="Supported Versions"></a>
<a href="https://github.com/JDKDigital/productive-bees/blob/1.20.0/LICENSE"><img src="https://img.shields.io/github/license/JDKDigital/productive-bees?style=flat&color=900c3f" alt="License"></a>
<a href="https://discord.gg/v2fVahY"><img src="https://img.shields.io/discord/756513972282195969?color=844685&label=Feedback%20%26%20Help&style=flat" alt="Discord"></a>
<a href="https://www.curseforge.com/minecraft/mc-mods/productivebees"><img src="http://cf.way2muchnoise.eu/short_productivebees.svg" alt="Curseforge Downloads"></a><br><br>

Here bee puns

### Modifying Productive Bees
Most aspects of Productive Bees can be modified with a data pack. An example datapack can be found here https://github.com/JDKDigital/productive-bees/tree/1.20.0/pb_datapack
Datapacks must be put in the datapacks folder inside the world folder and be enabled using the `/datapack enable` command.

#### Adding or removing bees

The first thing to do is to create a directory called productivebees on the same level as recipes and tags. In that folder you will put the json files for your bees. The filename will be the name of the bee clay.json becomes Clay Bee and green_grass.json becomes Green Grass Bee. The internal name you will later use in recipes will be productivebees:clay and productivebees:green_grass. There are not many fields in the json yet, but you must include the primary (head) and secondary (abdomen) colors. You can use conditions just like in recipes.
See this example `productivebees/productivebees/ender.json`
There are two other fields description which is the description shown in JEI and flowerTag which is the name of the block tag this bee will use as flowers. The tag name would be something like minecraft:flowers or c:storage_blocks/lead. It defaults to minecraft:flowers if nothing is defined.

Next you can make breeding recipes or item conversion recipes, depending on how you want to obtain the bee. The location for the breeding recipe is recipes/bee_breeding and for conversion recipes/bee_conversion. An example of breeding `productivebees/recipes/bee_breeding/constantan_bee.json` and conversion `productivebees/recipes/bee_conversion/manasteel_bee.json`

You will then need to make a recipe for the bees production when inside a hive, the recipe is located in recipes/bee_produce. You will most likely just follow this format `productivebees/recipes/bee_produce/constantan_bee.json`
The comb output has to be `productivebees:configurable_honeycomb` and the bee_type need to be the internal name of your bee.
item_output is the item produced if combs are disabled in the config.

Last you need a centrifuge recipe for your comb in recipes/centrifuge. For metals I usually make two recipes, one for the ingot and one for the dust (if any). The ingot recipe is disabled if a dust exist.
`productivebees/recipes/centrifuge/honeycomb_constantan.json`, `productivebees/recipes/centrifuge/honeycomb_constantan_dust.json`
The ingredient has to be productivebees:configurable_honeycomb and comb_type is the internal name of your bee.

Recipe for making comb blocks is done automatically.

Existing bees implemented using the data pack system can be removed by overriding their json file. Existing bees can be found in
 `productivebees/productivebees`
To disable a bee, copy the json file to your data pack and change the conditions to something that evaluates to false.

#### Changing the comb block recipes

By default it takes 4 honeycombs to make a comb block. To change the count you need to override recipes in `productivebees/recipes/comb_block`. `configurable_comb_block.json` and `configurable_honeycomb.json` has a count property which is the one used for calculating the data pack bees comb recipes.

#### Changing solitary bee spawns 

Recipes in `productivebees/recipes/bee_spawning` can be overridden to change which bees spawn from which nest. The repopulation cooldown can also be defined here.
