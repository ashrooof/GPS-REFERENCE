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

## Color and dimension caution

Binary XML refers to color and dimension resources by IDs (for example, `@0x7f050443` and `@0x7f0600d9`). This pass has not yet resolved the complete `resources.arsc` table into exact color hex values and dp values. Therefore no guessed palette or spacing values are being presented as original values.

## Next implementation sequence

1. Resolve the resource table and string table, including exact colors, dimensions, and text.
2. Map the remaining activity/dialog layouts to their screen navigation.
3. Rebuild the main map composition and overlay controls first.
4. Integrate the extracted original artwork into the ASH GPS resource tree.
5. Rebuild settings, history, search, route, and backup surfaces.
6. Remove advertising placements and ad SDK initialization from the independent implementation while preserving unrelated app functionality.
7. Run CI build and device-level behavior checks; do not mark parity complete based on compilation alone.
