# Grimur Change Hat
Minecraft 1.18, 1.19

Changes item's CustomModelData and item name with a command. Supports permissions using the generated config file.

## Usage

Start plugin in server plugins folder, once config file is created edit using labels below.

## Config

**default-hat-id: 1000** - default item data, item must be held with this item data for /changehat to work.

**default-item: "MUSIC_DISC_11"** - default item, item must be held for /changehat to work and gets its item data changed.

**error-sentence: "You must be holding a Test hat to use this command"** - error sentence when user does is not holding a default item.

## Commands
**/changehat** - list of available hats (**/changehat hatname**)

**/changehat reloadconfig** - reloads config.yml (must be op)
