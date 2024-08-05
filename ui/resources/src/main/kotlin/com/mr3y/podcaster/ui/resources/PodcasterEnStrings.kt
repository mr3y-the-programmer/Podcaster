package com.mr3y.podcaster.ui.resources

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import cafe.adriel.lyricist.LyricistStrings

@LyricistStrings(languageTag = "en", default = true)
val EnStrings = PodcasterStrings(
    tab_subscriptions_label = "Subscriptions",
    tab_explore_label = "Explore",
    tab_library_label = "Library",
    tab_settings_label = "Settings",
    subscriptions_refresh_result_error = "Couldn't refresh feeds",
    subscriptions_refresh_result_mixed = "Couldn't refresh some feeds",
    icon_menu_content_description = "Tap to open Navigation drawer",
    icon_settings_content_description = "Tap to navigate to settings",
    icon_navigate_up_content_description = "Tap to go to the previous screen",
    icon_theme_content_description = "Toggle App Theme",
    subscriptions_label = "Subscriptions",
    subscriptions_empty_list = "You aren't subscribed to any podcast.\nYour subscriptions will show up here.",
    subscriptions_episodes_empty_list = "Start subscribing podcasts by\n • Navigating to Explore tab\n • Or import your existing subscriptions collection from Settings tab -> then Import/Export.",
    generic_error_message = "Sorry, Something went wrong",
    retry_label = "Retry",
    currently_playing = "Currently Playing",
    buffering_playback = "Buffering...",
    search_for_podcast_placeholder = "Search for a podcast or add RSS Url",
    recent_searches_label = "Recent Searches",
    close_label = "CLOSE",
    feed_url_incorrect_message = "Make sure the feed url is correct and you're connected to Internet.",
    search_podcasts_empty_list = "No podcasts found matching your search.",
    podcast_details_refresh_result_error = "Something went wrong, refreshing failed!.",
    podcast_details_refresh_result_mixed = "Something went wrong.",
    subscribe_label = "Subscribe",
    unsubscribe_label = "Unsubscribe",
    about_label = "About",
    episodes_label = "Episodes",
    episode_details_refresh_result_error = "Something went wrong, refreshing failed!.",
    episode_details_refresh_result_mixed = "Something went wrong.",
    sync_work_notification_title = "Podcaster is Syncing your subscriptions",
    sync_work_notification_body = "Refreshing podcasts.",
    sync_work_notification_channel_name = "Sync",
    sync_work_notification_channel_description = "Background Refreshing tasks for Podcaster",
    download_work_notification_message = "Downloading episode/s currently in progress...",
    downloads_label = "Downloads",
    downloads_empty_list = "You have no episodes downloaded or still downloading",
    library_label = "Library",
    settings_label = "Settings",
    appearance_label = "Appearance",
    theme_heading = "Theme",
    theme_light_label = "Light",
    theme_dark_label = "Dark",
    theme_system_default_label = "System Default",
    dynamic_colors_label = "Dynamic color",
    dynamic_colors_on_label = "On",
    dynamic_colors_off_label = "Off",
    open_source_licenses_label = "Open source licenses",
    version_label = "Version",
    feedback_and_issues_label = "Feedback & Issues",
    privacy_policy_label = "Privacy Policy",
    powered_by_label = buildAnnotatedString {
        append("Powered by ")
        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
            append("PodcastIndex.org")
        }
    },
    import_export_label = "Import/Export Opml",
    import_label = "Import",
    export_label = "Export",
    import_notice = "If you're importing subscriptions collection make sure you're connected to the internet or the importing will fail",
    import_succeeded = "Importing subscriptions collection completed successfully!",
    import_network_error = "Importing collection failed! make sure you're connected to the internet.",
    import_empty_file_error = "Empty file! nothing to import",
    import_corrupted_file_error = "File may be corrupted! process failed.",
    import_unknown_error = "Sorry, Something went wrong",
    favorites_label = "Favorites"
)
