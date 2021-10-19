# slapmap

A Minecraft serverside mod for the Fabric Modloader.
Provides players a tool to import images from the internet into their worlds using Maps.

## Demo
https://user-images.githubusercontent.com/16215993/137979370-9f8b4977-a29e-4e66-826a-d51290bec580.mp4

## Usage

Standard: `/slap $imageName url=$imageURL`
Align:    `/slap $imageName url=$imageURL align=$alignment`
Width:    `/slap $imageName url=$imageURL width=$widthInBlocks`
Height:   `/slap $imageName url=$imageURL height=$heightInBlocks`
Combined: `/slap $imageName url=$imageURL align=$alignment width=$widthInBlocks height=$heightInBlocks`
Example: `/slap minecraft url=https://images-na.ssl-images-amazon.com/images/I/418cEZfh8-L.jpg align=bottomleft height=8`

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





