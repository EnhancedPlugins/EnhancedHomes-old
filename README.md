**Native Minecraft Version:** 1.21  
**Tested Minecraft Version:** 1.17, 1.18, 1.19, 1.20, 1.20.6
**Languages Supported:** English (French coming soon)  

![EnhancedHomes](https://media.discordapp.net/attachments/1250630591154749491/1250630845132308543/enhancedhomes.jpg?ex=666ba43d&is=666a52bd&hm=19fad3b06d0f0805fb389a3e66219c17625b45ffc6e3478731b8e00be75dd9bb&=&format=webp)

EnhancedHomes is a simple plugin that allows players to set homes with a name and teleport to them or to other players' homes. This plugin is very lightweight, easy to use, and comes with configuration options to customize it to your liking (homes limit, warmup, cross-world teleportation, etc).

# Features
- [X] **Set, manage, and teleport to homes.**  
This plugin allows players to set homes with a name and teleport to them, delete them, or do the same with other players' homes.
- [X] **Multi-world support.**  
Players can set homes in different worlds and teleport to them without any issues.
- [X] **Configurable homes limit, cross-world teleportation, and more.**  
Plugin can be fully customized to your liking with the configuration file but also with the permissions.
- [X] **Configurable messages.**  
All messages can be customized in the configuration file and prefix can be changed or removed.
- [X] **Lightweight and easy to use.**  
EnhancedHomes is a very lightweight plugin that is easy to use and doesn't require any setup.
- [X] **Active development.**  
  This plugin is actively developed and maintained. New features and bug fixes are released regularly. Do not hesitate to suggest new features or report bugs.

# Commands

| Command                                | Description          |
|----------------------------------------|----------------------|
| `/home <name> \|\| [player] <name>`    | Teleport to a home.  |
| `/homes [player]`                      | List homes.          |
| `/sethome <name>`                      | Set a home.          |
| `/delhome <name> \|\| [player] <name>` | Delete a home.       |
| `/enhancedhomesreload`                 | Reload the plugin.   |

Arguments between `<>` are required, and arguments between `[]` are optional. `||` stands for "or".

# Permissions

## Basic permissions

| Permission                           | Description         |
|--------------------------------------|---------------------|
| `enhancedhomes.home`                 | Teleport to a home. |
| `enhancedhomes.homes`                | List homes.         |
| `enhancedhomes.sethome`              | Set a home.         |
| `enhancedhomes.delhome`              | Delete a home.      |

## Admin permissions

| Permission                           | Description                  |
|--------------------------------------|------------------------------|
| `enhancedhomes.homes.other`          | List other players' homes.   |
| `enhancedhomes.delhome.other`        | Delete other players' homes. |
| `enhancedhomes.enhancedhomesreload`  | Reload the plugin.           |

## Modifier permissions

| Permission                             | Description                        |
|----------------------------------------|------------------------------------|
| `enhancedhomes.sethome.max.<number>`   | Set a maximum number of homes.     |
| `enhancedhomes.sethome.unlimited`      | Set an unlimited number of homes.  |
| `enhancedhomes.crossworldtp.bypass`    | Bypass cross-world teleportation.  |
| `enhancedhomes.warmup.bypass`          | Bypass teleportation warmup.       |

# Installation

1. Download **EnhancedHomes** from this page.
2. Place the downloaded file in your `plugins` folder.
3. Restart your server.
4. Enjoy!

# Support

If you need help with the plugin, have a suggestion, or found a bug, do not hesitate to contact us on Discord: @rvhoney, @nohmah.