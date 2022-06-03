# More Culling  
### A mod that changes how blockstate culling is handled in order to improve performance  
###### Culling is when you don't render things that the player can't see, to improve performance
  
**Warning this mod is still in alpha testing, make sure to report any bugs [here](https://github.com/fxmorin/moreculling/issues)!**  
  
## Current Bugs this fixes:  
[MC-249693](https://bugs.mojang.com/browse/MC-249693) - Chorus plant culling  
[MC-238739](https://bugs.mojang.com/browse/MC-238739) - Bamboo culling  
[MC-209633](https://bugs.mojang.com/browse/MC-209633) - Glass pane culling  
[MC-139620](https://bugs.mojang.com/browse/MC-139620) - Fence culling (except pressure plate)  
[MC-217653](https://bugs.mojang.com/browse/MC-217653) - End Rod culling (Not all but most)  
**Plus a lot more that haven't been reported**  

## Comparing Performance  
**Results may vary drastically!**  
Without MoreCulling, I get around ~136 frames  
![Normal - FPS ~136](https://github.com/fxmorin/MoreCulling/blob/master/images/normally.png)  
With MoreCulling, I get around ~210 frames  
![MoreCulling - FPS ~210](https://github.com/fxmorin/MoreCulling/blob/master/images/moreculling.png)  
Don't mind that bar, it was a screenshot.  
Do not expect these results all the time. Bamboo renders 2 extra faces for each block without MoreCulling, which is why the results are so drastic here!  
  
## More Info    
Feel free to contribute to the project!  
I'm also fine if you use this mod in your modpacks  

### My other optimization/bugfix mods:  
* [MemoryLeakFix](https://github.com/fxmorin/memoryLeakFix) - self-explanatory
* [Carpet-Fixes](https://github.com/fxmorin/carpet-fixes) - Largest Server-Side Bug fixing mod  
* [Blanket](https://github.com/BlanketMC/blanket-client-tweaks) - Client-side tweaks/fixes  

---
  
I don't give consent to Debugify to use this mod (or any mod) to merge my mod into another mod.  
Here's why: https://gist.github.com/fxmorin/9770473614e3e5e0703e44273dab33f7  
