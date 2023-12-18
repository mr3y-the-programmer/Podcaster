package com.mr3y.podcaster.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mr3y.podcaster.LocalStrings
import com.mr3y.podcaster.ui.theme.onPrimaryTertiary
import com.mr3y.podcaster.ui.theme.primaryTertiary

@Composable
fun Error(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = strings.generic_error_message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        ElevatedButton(
            onClick = onRetry,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryTertiary,
                contentColor = MaterialTheme.colorScheme.onPrimaryTertiary
            ),
        ) {
            Text(
                text = strings.retry_label,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
