# ASH GPS — Premium Edition

An independent Android rebuild based on the supplied GPS Location Faker 1.6.2 APK and the user's explicit direction to preserve its original visual experience and core functionality while removing advertising.

## Product requirements

- **Visual fidelity:** use the original app as the visual reference for its colors, typography, icons, spacing, page structure, dialogs, map controls, and navigation. Do not treat the current prototype's dark/mint styling as the approved target palette; verify the original resources/screens before applying colors.
- **No ads:** remove advertising from the rebuilt app. Do not show banners, native ads, rewarded-ad prompts, ad placeholders, or ad-related navigation. Core functions must not depend on watching ads.
- **Feature parity:** independently rebuild the original app's user-facing capabilities, including mock location, map interaction, route creation/playback, joystick movement, saved locations and routes, grouped history, search, GPS parameter controls, GPX import/export, and backup/restore, subject to verification of each behavior.
- **Brand:** app name is ASH GPS. Keep the original interaction model and appearance rather than redesigning the app into a different product.

## Current status

- Original Android Studio source: unavailable.
- Original input inspected: `GPS Location Faker_1.6.2.apks`.
- APK component/resource findings: `docs/reverse-engineering-notes.md`, `docs/resource-inventory.md`, and `docs/technical-audit.md`.
- Initial Android scaffold and mock-location prototype committed.
- First debug APK build succeeded on GitHub Actions (run `45406550969`).
- The initial prototype is **not** a visual or functional parity release and has not been physically device-tested.
- The original app's exact color values and detailed screen layouts still need resource decoding and screenshot-by-screenshot inspection before visual matching can be claimed.

## Implementation principles

1. Reconstruct the original screens and navigation from decoded resources and reference screenshots.
2. Keep ad SDKs, ad placements, ad prompts, and ad-only flows out of the new app.
3. Reimplement functionality cleanly; do not patch the original APK or bypass licensing, billing, or third-party service checks.
4. Validate mock-location behavior on real Android versions and devices.
5. Verify each feature before marking it complete.

## Planned milestones

1. Decode and document the original visual system and screen flows.
2. Rebuild the main map screen and its controls to match the reference.
3. Implement mock location, joystick, route editing/playback, saved places/routes, and history.
4. Implement search, GPS parameter settings, GPX import/export, and backup/restore.
5. Test on devices, then prepare a properly signed release build.

Map rendering and place search need a supported provider and any required API credentials. No working original Mapbox credential was recovered during the first inspection.

## Repository layout

- `app/` — Android application source.
- `docs/reverse-engineering-notes.md` — APK findings and implementation constraints.
- `docs/resource-inventory.md` — observed resource inventory.
- `docs/original-parity-requirements.md` — approved no-ads and visual/feature parity requirements.
- `.github/workflows/android-build.yml` — CI debug APK build.

This is a source-less independent rebuild. Exact parity must be demonstrated through implementation and testing, not assumed.
