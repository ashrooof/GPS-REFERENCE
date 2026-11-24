# APK resource inventory (first pass)

This inventory is based on filenames present in the supplied `base.apk`. Resource names are useful clues for reverse engineering, but are not proof of complete behavior.

## App-specific layouts observed

- `activity_main_binding.xml`, `activity_splash.xml`
- `fragment_map_page.xml`
- `layout_home_basic_title.xml`
- `layout_home_route_set.xml`
- `layout_route_mode_sel.xml`
- `dialog_map_page_menu.xml`, `dialog_map_type.xml`
- `dialog_open_dev_mock.xml`, `dialog_req_permission.xml`
- `gps_attr_set_dialog.xml`
- `mock_floating_view.xml`, `mocking_notification.xml`
- `activity_history.xml`, `fragment_history_group_list.xml`, `his_page_item.xml`, `item_history_location.xml`
- `activity_search.xml`, `item_location_search_result.xml`, `item_location_search_name.xml`
- `activity_setting.xml`
- `activity_data_backup.xml`
- `activity_iab.xml`, `activity_iab_sub.xml`

## Route and saved-location resources observed

- `item_route_list_layout.xml`
- `route_marker_pop.xml`
- `route_save_pop.xml`
- `save_route_confirm_dialog.xml`
- `location_save_pop.xml`
- `dialog_create_group.xml`

These names suggest map route editing, route markers, route saving, location saving, and grouping UI. Actual controls and route semantics require decoded resources and behavioral testing.

## Ad-related resource names observed

- `banner_with_loading.xml`
- `item_banner_ad_in_list.xml`
- `item_his_native_ad.xml`
- `item_in_list_native_ad.xml`
- `item_location_search_ad.xml`
- `native_card_ad_layout.xml`
- `watch_ad_confirm_dialog.xml`

This confirms that ad-oriented layouts are packaged. It does not establish which layouts are displayed in a particular session or whether ad delivery is remotely configured.

## Reverse-engineering limitations

The APK contains binary Android XML and DEX bytecode. Proper analysis needs Android resource/DEX tooling (for example, apktool and JADX) and then careful mapping of decompiled code to resources. Those tools are not installed in the current execution environment, so this inventory is intentionally limited to the archive's filenames and previously extracted manifest identifiers.

## Build plan

1. Acquire and install APK decoding tools in a controlled environment.
2. Decode resources and manifest; map layouts, strings, drawables, navigation, and app-specific classes.
3. Inspect decompiled code to identify the actual data model and mock-location lifecycle.
4. Implement a clean, independently buildable Android project; do not reuse or strip third-party SDK code without appropriate rights.
5. Add tests for permission flows, mock-location start/stop, persistence, route behavior, backup/restore, and Android-version compatibility.
6. Produce signed debug builds for user testing before any release build.


## Decoded main-screen structure (follow-up pass)

A direct binary-XML pass confirms that the main experience is map-first:
- `activity_main_binding.xml` provides a full-screen content frame.
- `fragment_map_page.xml` embeds a Google Maps `SupportMapFragment` and overlays map controls/cards.
- `layout_home_basic_title.xml` and `layout_home_route_set.xml` are separate basic-location and route-setup cards.
- `mock_floating_view.xml` includes the custom `JoystickView` and its directional labels/actions.
- `dialog_map_page_menu.xml` includes switch-style menu items; `dialog_map_type.xml` handles map-type selection.

See [decoded-layout-audit.md](decoded-layout-audit.md) for the structural findings and remaining resource-table work. Exact color hex values and dimensions are not yet resolved, so the provisional prototype palette must not be treated as original styling.
