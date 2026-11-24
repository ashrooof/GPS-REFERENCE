# Decoded layout audit — GPS Location Faker 1.6.2

This pass reads the binary Android XML in the supplied `base.apk` directly. It is a structural audit of the packaged layouts, not a claim that every view or hidden state has been reconstructed.

## Main screen composition

- `activity_main_binding.xml` is a full-screen root containing the main content frame.
- `fragment_map_page.xml` hosts a Google Maps `SupportMapFragment` as the central full-screen surface.
- The map fragment layers controls and cards over the map rather than replacing it with a coordinate-entry form.
- `layout_home_basic_title.xml` is the compact/basic location card.
- `layout_home_route_set.xml` is the route-setup card and contains a scrollable route-control region, route-point controls, a timer/readout area, and related actions.
- `dialog_map_page_menu.xml` contains a menu with switch-style controls.
- `dialog_map_type.xml` is the map-type selection surface.
- `mock_floating_view.xml` contains the custom `JoystickView`, directional labels, and floating actions.

## Confirmed visual assets

The extracted app-specific drawable set includes:
- Home actions: favorite, joystick, menu, rate, settings, share.
- Joystick states and floating joystick actions.
- Map layers and map-center marker.
- Route point, pin, reset, round-trip, route-to-point, expand/fold and route-enhancement artwork.
- Walking, bicycle, car and electric-motorcycle speed icons.
- Default, satellite and terrain map-type artwork.
- Mock-location target lock/unlock and app notification logo.

The accompanying `ASH-GPS-original-ui-assets.zip` is a curated export of 38 original image resources, preserving their APK resource paths.

## Interaction/feature surfaces found

The layout inventory supports the existence of these UI areas:
1. Full-screen map with overlaid home cards and map controls.
2. Basic location selection and route setup as distinct card states.
3. Route mode selection and route controls.
4. Map-type selection.
5. Mock-location joystick overlay.
6. Settings, history, search and backup/restore activities.
7. GPS attribute configuration dialog.

The resource names and layouts establish that these surfaces are packaged. They do not alone prove all business rules or edge-case behavior.

## Resolved original color values

The `resources.arsc` table has now been parsed for the app's named color resources. Confirmed original values include:

| Resource name | Value |
|---|---|
| `black_222222` | `#FF222222` |
| `blue_0066E5` | `#FF0066E5` |
| `blue_0076ff` | `#FF0076FF` |
| `blue_00AEFF` | `#FF00AEFF` |
| `blue_1780F6` | `#FF1780F6` |
| `blue_2A84F6` | `#FF2A84F6` |
| `grey_dcdcdc` | `#FFDCDCDC` |
| `light_green_32CD32` | `#FF32CD32` |
| `red_FF3333` | `#FFFF3333` |
| `route_config_seek` | `#FF509AE9` |
| `white` | `#FFFFFFFF` |
| `white_alp20` | `#CCFFFFFF` |
| `white_alp40` | `#80FFFFFF` |
| `white_js_btn` | `#A0FFFFFF` |
| `yellow_FFA500` | `#FFFFA500` |

These values are preserved in `app/src/main/res/values/original_colors.xml`. This is a recovered resource palette, not a claim that every listed color is used on every screen. Exact dimension values and full resource-to-view mapping remain in progress.

## Maps configuration note

The original manifest declares Google Maps and Places components and contains a Maps API key. The original key will not be copied into ASH GPS. A separately owned and appropriately restricted Google Maps API key is required for a production map build; no secret from the uploaded APK is exposed or reused.

The original manifest also includes Play licensing/billing and PairIP application components. These are not advertising SDKs; they must not be removed or bypassed as part of the ads-only scope without separate authorization.

## Next implementation sequence

1. Resolve the resource table and string table, including exact colors, dimensions, and text.
2. Map the remaining activity/dialog layouts to their screen navigation.
3. Rebuild the main map composition and overlay controls first.
4. Integrate the extracted original artwork into the ASH GPS resource tree.
5. Rebuild settings, history, search, route, and backup surfaces.
6. Remove advertising placements and ad SDK initialization from the independent implementation while preserving unrelated app functionality.
7. Run CI build and device-level behavior checks; do not mark parity complete based on compilation alone.
