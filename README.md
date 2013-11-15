CustomHealth
============

The Bukkit Plugin for customizing food effects.

Main project page: http://dev.bukkit.org/bukkit-plugins/custom-health/

<h2>Most important classes and files</h2>
<a href="https://github.com/felixfritz/CustomHealth/blob/master/src/at/felixfritz/customhealth/CustomHealth.java">CustomHealth.java</a> - Main plugin class, contains onEnable, onDisable method<br>
<a href="https://github.com/felixfritz/CustomHealth/blob/master/src/at/felixfritz/customhealth/foodtypes/FoodValue.java">FoodValue.java</a> - Contains all the values for a food item, regenerated health, hunger, etc.<br>
<a href="https://github.com/felixfritz/CustomHealth/blob/master/src/at/felixfritz/customhealth/eventlisteners/MainListener.java">MainListener.java</a> - Executes everything when food item is eaten

<h2>Less important classes and files (which are important too)</h2>
<a href="https://github.com/felixfritz/CustomHealth/blob/master/src/at/felixfritz/customhealth/Database.java">Database.java</a> - Saves all informations about food items and worlds as static maps<br>
<a href="https://github.com/felixfritz/CustomHealth/blob/master/src/at/felixfritz/customhealth/util/RandomValue.java">RandomValue.java</a> - Used for randomization, random values between a minimum and maximum<br>
<a href="https://github.com/felixfritz/CustomHealth/blob/master/src/at/felixfritz/customhealth/util/Updater.java">Updater.java</a> - Update checker that I didn't write. More about it <a href="https://forums.bukkit.org/threads/updater-2-0-easy-safe-and-policy-compliant-auto-updating-for-your-plugins-new.96681/">here</a>

<a href="https://github.com/felixfritz/CustomHealth/blob/master/src/at/felixfritz/customhealth/foodtypes/PotionValue.java">PotionValue.java</a> - All potion attributes that are saved in the <a href="https://github.com/felixfritz/CustomHealth/blob/master/src/at/felixfritz/customhealth/foodtypes/FoodValue.java">FoodValue</a><br>
<a href="https://github.com/felixfritz/CustomHealth/blob/master/src/at/felixfritz/customhealth/foodtypes/EffectValue.java">EffectValue.java</a> - All additional effect attributes that are saved in the <a href="https://github.com/felixfritz/CustomHealth/blob/master/src/at/felixfritz/customhealth/foodtypes/FoodValue.java">FoodValue</a>. Class is abstract, all effects in <a href="https://github.com/felixfritz/CustomHealth/tree/master/src/at/felixfritz/customhealth/foodtypes/effects">here</a> are the child classes<br>

<a href="https://github.com/felixfritz/CustomHealth/blob/master/src/at/felixfritz/customhealth/eventlisteners/FoodChanger.java">FoodChanger.java</a> - Listener class, only called if at least one of the worlds have "get-hungry" disabled (check settings.change-food-level in <a href="https://github.com/felixfritz/CustomHealth/blob/master/template0x0159.yml#L25">template</a> file)<br>
<a href="https://github.com/felixfritz/CustomHealth/blob/master/src/at/felixfritz/customhealth/eventlisteners/HeartChanger.java">HeartChanger.java</a> - Listener class, only called if at least one of the worlds have heart regeneration turned off (check settings.regain-health in <a href="https://github.com/felixfritz/CustomHealth/blob/master/template0x0159.yml#L25">template</a> file)<br>

<a href="https://github.com/felixfritz/CustomHealth/blob/master/src/at/felixfritz/customhealth/commands/CommandMain.java">CommandMain.java</a> - Main command class, contains commands such as <b>/ch create</b>, <b>/ch rename</b>, <b>/ch reload</b>, etc.
