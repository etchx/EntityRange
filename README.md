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

### Hit logging
- Start and stop logging with `/er hitlog record`
- Hitlog files are stored in a dedicated `/hitlogs` directory
- Hitlog data include hit time, charge, hit type, distance, and damage
- Ending recording displays statistics of the recording session, including average distance and DPS
- Display the statistics of a previous log with `/er hitlog stats [filename]`

### Customizability
- Commands:

`/er toggle [chat|distance|hits|long]`

## TODO
- Use opponent's velocity, ping, and a custom offset (like reaction time) to enable optimal range control [tentative]
- Improve customizability (drag & drop UI, colors)
