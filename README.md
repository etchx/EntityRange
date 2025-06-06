# EntityRange

The ultimate reach indicator mod

## Current Features
### Distance indicator
- Shows reach distance to targeted entity up to 80 blocks away
- Can toggle off long distance to show default distance (4.5 blocks)
- Accurate to aiming
- Works while spectating an entity
- Turns red when in hit range (based on entity interaction distance)
- Disabled when user has blindness or target has invisibility
- Can be toggled off

### Hit indicator
- Shows distance of last hit
- Works for projectiles, not just melee
- Shooting at a non-player entity with an arrow or using non-arrow projectiles such as tridents or snowballs (whenever the ding sound doesn't play) may be inaccurate on servers due to desyncs
- Chat Mode shows hit distances in chat, can be toggled on
- Can be toggled off

### Customizability
- Commands:

`\er toggle [chat|distance|hit|long]`

## TODO
- Add the ability to log hits and calculate some useful statistics for hit lengths, timings, and types
- Use opponent's velocity, ping, and a custom offset (like reaction time) to enable optimal range control [tentative]
- Improve customizability (drag & drop UI, colors)
