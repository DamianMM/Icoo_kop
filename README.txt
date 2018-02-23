## Group 23 IV1351 Datalagring Project Application ##

Program requirements:

1. Icookop_executable.jar requires a JRE to run.

2. Icooköp_v8.accdb must be present in the root folder of Icookop_executable.jar. Failing this, Icookop_executable.jar will not be able to fetch any data from the database.



New in beta v0.2:

- New: On startup, ChoiceBox1 now dynamically fills dropdown item data by calling getProductTypes.
- New: On startup, ChoiceBox2 now dynamically fills dropdown item data by calling getStores.
- Fixed insertStamkund method params.
- Minor tweaks.