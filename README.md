# EntityRange

The ultimate reach indicator mod

## Current Features
### Distance indicator
- Shows reach distance to targeted entity (within 4.5 blocks)
- Enable "use-long-distance" in config to increase range to 80 blocks
- Accurate to aiming
- Works while spectating an entity
- Turns red when in hit range (3 blocks)
- Can be disabled in config

### Hit indicator
- Shows distance of last hit
- Works for projectiles, not just melee
- Shooting at a non-player entity with an arrow or using non-arrow projectiles such as tridents or snowballs (whenever the ding sound doesn't play) may be inaccurate on servers due to desyncs
- Chat Mode shows hit distances in chat, enabled in config
- Can be disabled in config

## TODO
- Update mod to newer versions and add support for custom reach values
- Disable distance indicator when user has blindness or opponent has invisibility
- Use opponent's velocity and a custom offset (like reaction time) to enable optimal range control [tentative]
- Improve customizability (drag & drop UI, client commands, colors)
