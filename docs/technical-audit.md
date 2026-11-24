# Initial technical audit

## Input examined

- File: `GPS Location Faker_1.6.2.apks`
- Container format: ZIP-based APKS archive
- Contents: `base.apk` and `split_config.arm64_v8a.apk`
- App version string found in the binary manifest: `1.6.2`
- Package identifier found in the binary manifest: `fake.gps.location.emulator`

## Components identified in the manifest

The manifest contains app components with these names:

- `pages.main.MainActivityLatest`
- `pages.his.HistoryActivity`
- `pages.search.SearchActivity`
- `pages.setting.SettingsActivity`
- `pages.backup.DataBackupActivity`
- `pages.iab.IabActivity`
- `pages.web.WebActivity`
- `service.MockLocationService`

These names indicate the presence of main, history, search, settings, backup, in-app-billing, web, and mock-location components. They do not by themselves prove every user-facing behavior or its implementation details.

## Permissions and integrations observed

The binary manifest includes location-related permissions such as:

- `ACCESS_COARSE_LOCATION`
- `ACCESS_FINE_LOCATION`
- `ACCESS_MOCK_LOCATION`

It also includes network, notification, foreground-service, overlay, billing, and advertising-related permissions.

The package contains references to advertising/mediation SDKs and components, including AppLovin, Google Mobile Ads, Facebook Audience Network, and Vungle. This is evidence that advertising SDK code is bundled; it does not identify every active placement or prove which networks serve ads at runtime.

## Important build limitations

- This archive contains compiled APKs, not Gradle project files, Kotlin/Java source, resources in editable project form, or the original build configuration.
- DEX strings include application identifiers and location-related symbols, but this is not a substitute for readable source code.
- The inspected DEX contains release/R8 optimization metadata, so some application code may be obfuscated or optimized.
- No Android build toolchain was found in the inspection environment during this initial pass.
- The repository was empty before this audit was added.

## Licensing and authorization boundary

The manifest includes a Pairip licensing-related application component. This audit records its presence only. It does not attempt to remove, bypass, patch, or disable licensing or purchase checks. Any ad-free Premium implementation should be made through the authorized source project and developer-approved configuration.

## Recommended next step

Request the original Android Studio project from the developer, including Gradle files, source code, resources, and any required configuration with secrets removed or supplied securely. Once available, compare its feature set against the installed app and create a reproducible build before redesign work begins.
