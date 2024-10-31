# List of commands

> The following commands '<>' denotes required parameters, '[]' denotes optional parameters

## Dominion Management

| Function                                           | Command                                                      | Note                                                                                                                                       |
|----------------------------------------------------|--------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| Create a dominion                                  | `/dominion create <dom_name>`                                |
| Automatically create a dominion                    | `/dominion auto_create <dom_name>`                           |
| Create a sub-dominion                              | `/dominion create_sub <sub-dom_name> [parent dom_name]`      | If the parent dom_name is not specified, the current dominion will be used by default;                                                     |
| Automatically create a sub-dominion                | `/dominion auto_create_sub <sub-dom_name> [parent dom_name]` | If the parent dom_name is not specified, the current dominion will be used by default;                                                     |
| List territories                                   | `/dominion list`                                             |
| Manage territories                                 | `/dominion manage <dom_name>`                                |
| Set visitor permissions for a dominion             | `/dominion set <flag_name> <true/false> [dom_name]`          | If the dom_name is not specified, the current dominion will be used as the default;                                                        |
| Expand the dominion in the direction of the view   | `/dominion expand [size] [dom_name]`                         | If the size is not specified, the default is 10; if the dom_name is not specified, the default is the current dominion;                    |
| Contract the dominion in the direction of the view | `/dominion contract [size] [dom_name]`                       | If the size is not specified, the default is 10; if the dom_name is not specified, the default is the current dominion;                    |
| Delete the dominion                                | `/dominion delete <dom_name>`                                |
| Set the prompt message for entering the dominion   | `/dominion set_enter_msg <enter_msg> [dom_name]`             | If the dom_name is not specified, the default is the current dominion;                                                                     |
| Set the prompt message for leaving the dominion    | `/dominion set_leave_msg <leave_msg> [dom_name]`             | If the dom_name is not specified, the default is the current dominion;                                                                     |
| Set the teleport point of the dominion             | `/dominion set_tp_location [dom_name]`                       | Set the dominion's teleport point to the current location; if the dom_name is not specified, the default is the current dominion;          |
| Rename the dominion                                | `/dominion rename <old_name> <new_name>`                     |
| Transfer the dominion to another player            | `/dominion give <dom_name> <player_name>`                    |
| Set the dominion's color on the satellite map      | `/dominion set_map_color <hex_color> [dom_name]`             | The color needs to be a hexadecimal color value, such as `#ff0000`; if the dom_name is not specified, the default is the current dominion; |

## Member Management

| Function                  | Command                                                                       | Note |
|---------------------------|-------------------------------------------------------------------------------|------|
| Add members               | `/dominion member add <dom_name> <player_name>`                               |
| Remove members            | `/dominion member remove <dom_name> <player_name>`                            |
| List dominion members     | `/dominion member list <dom_name>`                                            |
| Set member permissions    | `/dominion member set_flag <dom_name> <player_name> <flag_name> <true/false>` |
| Apply permission template | `/dominion member apply_template <dom_name> <player_name> <template_name>`    |

## Permission group management

| Function                         | Command                                                                     | Note |
|----------------------------------|-----------------------------------------------------------------------------|------|
| Create a permission group        | `/dominion group create <dom_name> <group_name>`                            |
| Delete a permission group        | `/dominion group delete <dom_name> <group_name>`                            |
| Add a permission group member    | `/dominion group add_member <dom_name> <group_name> <player_name>`          |
| Remove a permission group member | `/dominion group remove_member <dom_name> <group_name> <player_name>`       |
| Set permission group permissions | `/dominion group set_flag <dom_name> <group_name> <flag_name> <true/false>` |
| List dominion permission groups  | `/dominion group list <dom_name>`                                           |

## Miscellaneous

| Function                     | Command                                                                | Note                                                                            |
|------------------------------|------------------------------------------------------------------------|---------------------------------------------------------------------------------|
| Teleport to a dominion       | `/dominion tp <dom_name>`                                              |
| View dominion information    | `/dominion info [dom_name]`                                            | If the dom_name is not specified, the current dominion will be used by default; |
| Create a permission template | `/dominion template create <template_name>`                            |
| Delete a permission template | `/dominion template delete <template_name>`                            |
| List permission templates    | `/dominion template list`                                              |
| Set permission template      | `/dominion template set_flag <template_name> <flag_name> <true/false>` |
| Migrate from res             | `/dominion migrate <res_name>`                                         | Requires the administrator to enable dominion migration to use;                 |
| Use permission group title   | `/dominion use_title <group_id>`                                       | Requires the administrator to enable permission group title to use;             |

## Administrator commands

| Function                  | Command                   | Note                                                                                |
|---------------------------|---------------------------|-------------------------------------------------------------------------------------|
| Reload configuration file | `/dominion reload_config` | Most configuration changes require reloading the configuration file to take effect; |
| Reload cache              | `/dominion reload_cache`  | Sometimes it can solve some problems;                                               |
| Export (backup) database  | `/dominion export_db`     | Export the database to a file for migration, backup and other operations;           |
| Import database from file | `/dominion import_db`     | Import the database to a file for migration, backup and other operations;           |
