package com.mr3y.podcaster.ui.resources

import androidx.compose.ui.text.AnnotatedString

data class PodcasterStrings(
    val tab_subscriptions_label: String,
    val tab_explore_label: String,
    val tab_library_label: String,
    val tab_settings_label: String,
    val subscriptions_refresh_result_error: String,
    val subscriptions_refresh_result_mixed: String,
    val icon_menu_content_description: String,
    val icon_settings_content_description: String,
    val icon_navigate_up_content_description: String,
    val icon_theme_content_description: String,
    val subscriptions_label: String,
    val subscriptions_empty_list: String,
    val subscriptions_episodes_empty_list: String,
    val generic_error_message: String,
    val retry_label: String,
    val currently_playing: String,
    val buffering_playback: String,
    val navigate_to_episode_a11y_label: (String) -> String,
    val search_for_podcast_placeholder: String,
    val recent_searches_label: String,
    val close_label: String,
    val feed_url_incorrect_message: String,
    val search_podcasts_empty_list: String,
    val podcast_details_refresh_result_error: String,
    val podcast_details_refresh_result_mixed: String,
    val subscribe_label: String,
    val unsubscribe_label: String,
    val about_label: String,
    val episodes_label: String,
    val episode_details_refresh_result_error: String,
    val episode_details_refresh_result_mixed: String,
    val sync_work_notification_title: String,
    val sync_work_notification_body: String,
    val sync_work_notification_channel_name: String,
    val sync_work_notification_channel_description: String,
    val download_work_notification_message: String,
    val downloads_label: String,
    val downloads_empty_list: String,
    val library_label: String,
    val settings_label: String,
    val appearance_label: String,
    val theme_heading: String,
    val theme_light_label: String,
    val theme_dark_label: String,
    val theme_system_default_label: String,
    val dynamic_colors_label: String,
    val dynamic_colors_on_label: String,
    val dynamic_colors_off_label: String,
    val open_source_licenses_label: String,
    val version_label: String,
    val feedback_and_issues_label: String,
    val privacy_policy_label: String,
    val powered_by_label: AnnotatedString,
    val import_export_label: String,
    val import_label: String,
    val export_label: String,
    val import_notice: String,
    val import_succeeded: String,
    val import_network_error: String,
    val import_empty_file_error: String,
    val import_corrupted_file_error: String,
    val import_unknown_error: String,
    val favorites_label: String,
    val favorites_empty_list: String,
    val share_label: String,
    val icon_more_options_content_description: String,
)
