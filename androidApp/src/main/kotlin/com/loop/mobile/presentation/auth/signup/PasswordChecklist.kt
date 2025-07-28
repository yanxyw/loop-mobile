package com.loop.mobile.presentation.auth.signup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.loop.mobile.R
import com.loop.mobile.domain.validation.PasswordRequirement

@Composable
fun PasswordChecklist(requirements: List<PasswordRequirement>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Intro text
        Text(
            text = "Your password must include:",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 14.dp)
        )

        // Checklist items
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            requirements.forEach { req ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = if (req.isSatisfied) R.drawable.checked else R.drawable.circle),
                        contentDescription = null,
                        tint = if (req.isSatisfied) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = req.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
