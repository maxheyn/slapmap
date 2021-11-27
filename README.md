# slapmap

A Minecraft serverside mod for the Fabric Modloader.
Provides players a tool to import images from the internet into their worlds using Maps. 
Originally created by [kalkronline](https://github.com/kalkronline/slapmap)

## Demo
https://user-images.githubusercontent.com/16215993/137979370-9f8b4977-a29e-4e66-826a-d51290bec580.mp4

## Usage

Standard: `/slap $imageName url=$imageURL`<br>
Align:    `/slap $imageName url=$imageURL align=$alignment`<br>
Width:    `/slap $imageName url=$imageURL width=$widthInBlocks`<br>
Height:   `/slap $imageName url=$imageURL height=$heightInBlocks`<br>
Combined: `/slap $imageName url=$imageURL align=$alignment width=$widthInBlocks height=$heightInBlocks`<br>
Example: `/slap minecraft url=https://images-na.ssl-images-amazon.com/images/I/418cEZfh8-L.jpg align=bottomleft height=8`<br>

- `$imageName` is a descriptive name of your image.
   - minecraft, coolpic, logo, etc.
- `$imageURL` is the actual URL of the image from the web.
   - https://images-na.ssl-images-amazon.com/images/I/418cEZfh8-L.jpg
- `$alignment` is a list of options from which the image will be aligned. 
   - `bottom`
   - `bottomleft`
   - `bottomright`
   - `center`
   - `left`
   - `right`
   - `top`
   - `topleft`
   - `topright `
- `$widthInBlocks` and `$heightInBlocks` are how wide or tall the picture should be in blocks. If you only specify one of them, the aspect ratio of the image is maintained in the dimension not specified. It is recommended to specify both so that they align within the block grid of the world.

## Config
The default config is below, with a comment to explain.
```json5
{
  "MaxWidth": 4,
  "MaxHeight": 4,
  "PermLevel": 4,
  "MultiplayerWorldName": "world",
  "IsDefaultSettings": false
}
```
- `MaxWidth` and `MaxHeight` are the limits to how wide or tall imported images can be when creating a new slapmap.
   - If you import a new image and specify `height=<number_larger_than_MaxHeight>`, it will clamp it to MaxHeight and provide a message letting you know that the image was resized based on the values in the config. It will also automatically resize images if they do not have a specified height but would have otherwise been larger than the set values.
   - You can change these live and save the file without needing to restart the server or use any commands to apply these. Once you change the values and save, any new slapmaps made will be subject to these restrictions.
- `PermLevel` is the PermissionLevel needed to use any of slapmap's commands. 
   - The default is 4, which means you need to be an operator with permission level 4 (the default operator permission level) to use the commands.
   - To allow anyone to make slapmaps (dangerous on larger servers or with untrustworthy players), set this value to 0.
   - You can set it to values 1-3 as well if you use the [other operator permission levels](https://gaming.stackexchange.com/questions/138602/what-does-op-permission-level-do) on your server.
- `MultiplayerWorldName` is used for multiplayer worlds is used to make sure the slapmap database is placed in the correct directory. It defaults to `world`.
   - This value should match whatever you have `level-name` set as in your `server.properties` file. If it does not match, your slapmaps will not be stored properly and you will lose any access to them via commands on restarts.
- `IsDefaultSettings` is a debug value. Setting this to true and restarting your server should reset the config the default settings. You probably won't need to touch this.

