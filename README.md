If you're a developer and wish to use this plugin's API for your project, you must use Maven. This is obvious for most, but if you're not using it, I highly recommend you start doing so! (and if you use Gradle... I'm sorry for you)

In the <repositories> tag in the pom.xml, add this code:
```xml
<repository>
  <id>party</id>
  <url>https://github.com/HopperElec/Party</url>
</repository>```

And in the <dependencies> tag, add this code:
```xml
<dependency>
  <groupId>uk.co.hopperelec.mc</groupId>
  <artifactId>party</artifactId>
  <version>1.0.3</version>
</dependency>```

Once you've added it through Maven and refreshed it, you then need to add it as a dependency in the plugin.yml. To do this, simply add this:
```yaml
depend: [HopperParty]```

Now you should be good to go!

To access the plugin, use this code:
```java
Bukkit.getPluginManager().getPlugin("HopperParty");```

And cast it to (HopperParty). I recommend, as a safety precaution, to makes sure the plugin isn't null, though. So you should end up with code looking like this:
```java
final Plugin partyPlugin = Bukkit.getPluginManager().getPlugin("HopperParty");
if (partyPlugin == null) {
  System.out.println("Failed to get party plugin class! Cancelling start-up of the plugin!");
  setEnabled(false); return;
}
HopperParty partyClass = (HopperParty) partyPlugin;```

From there, your IDE should be able to guide you after typing partyClass then a dot somewhere in your code! Have fun :D
