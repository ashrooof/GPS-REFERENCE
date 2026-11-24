# ASH GPS — Reverse Engineering Notes

## Input examined
- Original upload: `GPS Location Faker_1.6.2.apks`
- Archive contains `base.apk` and `split_config.arm64_v8a.apk`.
- Base APK contains two primary DEX files plus an advertising-network DEX under assets.
- Original package ID: `fake.gps.location.emulator`
- Original version: `1.6.2`

## Confirmed application-owned classes
The DEX class definitions include:
- `base/FakeApp`, `base/BaseActivity`, `base/BaseFragment`
- `pages/main/MainActivityLatest`
- `pages/map/MapFragment`
- `pages/his/HistoryActivity`, `pages/his/HistoryGroupListFragment`, `pages/his/CreateGroupDialog`
- `pages/search/SearchActivity`
- `pages/setting/SettingsActivity`
- `pages/backup/DataBackupActivity`
- `pages/iab/IabActivity`
- `pages/dialog/MapTypeSelDialog`
- Map controls/dialogs: accuracy, altitude, bearing, interval, joystick speed, map menu, save location, save route, and developer mock-location prompt
- `service/MockLocationService`
- `view/JoystickView`, `view/SwitchButton`

## Confirmed feature clues from code/resources
- Location mocking and route simulation are core features.
- Saved locations and saved routes are represented in the app's persistence layer.
- History supports groups.
- GPX import and export code paths exist.
- Backup/restore code paths exist.
- Search strings and classes indicate OpenStreetMap search and Google Places integration.
- Map type selection and map-related settings exist.
- Tunable simulation properties include accuracy, altitude, bearing, interval, and joystick speed.

These findings are based on class descriptors, resource names, and embedded strings. They do not by themselves prove every edge case or exact UI behavior.

## Dependencies and constraints
- The original APK contains ad-network SDK code and billing-related components.
- The original app references a Mapbox token, but a working token was not recovered in this pass.
- The original Android Studio project, Gradle files, and source repository were not supplied.
- The replacement project uses a new application ID: `com.ashgps.app`.
- This is a clean implementation, not a repackaging of the original APK.
- No attempt will be made to bypass purchase/licensing checks or third-party service restrictions.

## Implementation order
1. Establish a reproducible Android build and ASH GPS visual identity.
2. Implement foreground mock-location control using Android's documented test-provider APIs. The user must explicitly enable ASH GPS as the mock-location app in Developer Options.
3. Add persistent saved locations and history.
4. Add route playback, GPX import/export, grouped history, and backup/restore.
5. Integrate a map/search provider after choosing a provider and configuring the required API credentials.
6. Test on physical Android devices and document supported Android versions.
