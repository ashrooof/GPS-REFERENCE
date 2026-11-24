# Original-app parity requirements

## User-approved direction

ASH GPS should retain the original GPS Location Faker 1.6.2 experience as closely as a clean rebuild can: same colors, overall visual language, screen organization, controls, and user-facing functions. Advertising must be absent.

## Visual acceptance criteria

- Treat the supplied original APK and its decoded resources/screens as the source of truth.
- Match original color roles and values, typography, icon style, component shapes, spacing, and navigation.
- Reproduce the original main map layout, floating controls, dialogs, history/search/settings/backup screens, and route-editing surfaces.
- Do not use the current prototype's dark navy and mint palette as a substitute for the original palette unless resource inspection confirms that it is correct.
- Keep ASH GPS branding limited to appropriate app identity surfaces; do not redesign the whole interface.

## Advertising removal

The rebuilt app must contain no:
- banner, native, interstitial, or rewarded ads;
- ad SDK integrations or ad network initialization;
- ad placeholders, empty ad containers, or reserved ad slots;
- watch-an-ad prompts or feature locks that require ad viewing.

Removing advertising must not remove or disable core non-ad functionality.

## Functionality to rebuild and verify

The APK resource/class inventory indicates the following areas. These are implementation targets, not claims that they are already complete:

1. Set, start, update, and stop mock GPS location.
2. Map screen, map-type selection, and map controls.
3. Route creation, route markers, route saving, and route playback/simulation.
4. Joystick-based location movement and configurable movement speed.
5. Save locations and organize location history into groups.
6. Search for places and select a result on the map.
7. Configure accuracy, altitude, bearing, update interval, and related simulation parameters.
8. GPX import/export.
9. Backup and restore app data.

## Current implementation gap

The first ASH GPS debug build is only a minimal scaffold with coordinate entry, a basic saved-coordinate list, and a foreground mock-location service. It does not yet meet visual or functional parity. The current screen styling is provisional, not verified against the original app.

## Verification rule

A feature is complete only after it has been implemented and exercised. For UI parity, compare corresponding original and rebuilt screens. For behavior, test success, cancellation, permission denial, invalid input, persistence, and relevant Android-version/device differences.
