# ASH GPS — Premium Edition

An independent Android rebuild based on the authorized redesign brief and analysis of the supplied GPS Location Faker 1.6.2 APK package.

## Current status

- Original Android Studio source: unavailable.
- Original input inspected: `GPS Location Faker_1.6.2.apks`.
- Initial reverse-engineering inventory: documented in `docs/reverse-engineering-notes.md`.
- New Android project scaffold: committed.
- First implementation milestone: branded ASH GPS home screen, coordinate entry, saved-coordinate history, and a foreground mock-location service using Android test-provider APIs.
- CI build: GitHub Actions workflow added; first run is being checked.
- This is a clean implementation. The original APK has not been patched or redistributed.

## Important setup

To test mock locations on a device, grant location permission and explicitly select **ASH GPS** in Android Developer Options → Select mock location app. Android controls this setting; the app does not bypass it.

## Planned milestones

1. Verify the first Android debug build and test the mock-location service on a physical device.
2. Add map rendering, place search, route creation/playback, and joystick controls.
3. Add grouped history, GPX import/export, and backup/restore.
4. Add device/version compatibility testing and prepare a signed release build.

Map rendering and place search need a chosen map/search provider and any required API credentials. No working original Mapbox credential was recovered during the first inspection.

## Repository layout

- `app/` — Android application source.
- `docs/reverse-engineering-notes.md` — APK findings and implementation constraints.
- `docs/technical-audit.md` — initial package/security/component audit.
- `.github/workflows/android-build.yml` — CI debug APK build.

The source-less rebuild may not reproduce every behavior of the original application exactly. Features will be verified incrementally rather than assumed.
