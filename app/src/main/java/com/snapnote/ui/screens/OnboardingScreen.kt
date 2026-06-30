package com.snapnote.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snapnote.R
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val emoji: String,
    val titleRes: Int,
    val descriptionRes: Int,
    val accentColor: Color
)

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pages = listOf(
        OnboardingPage("📸", R.string.onboarding_title_1, R.string.onboarding_desc_1, Color(0xFF5B5891)),
        OnboardingPage("🔍", R.string.onboarding_title_2, R.string.onboarding_desc_2, Color(0xFF3D6B8A)),
        OnboardingPage("🏷️", R.string.onboarding_title_3, R.string.onboarding_desc_3, Color(0xFF7D5260)),
        OnboardingPage("🔎", R.string.onboarding_title_4, R.string.onboarding_desc_4, Color(0xFF4A6741)),
        OnboardingPage("✏️", R.string.onboarding_title_5, R.string.onboarding_desc_5, Color(0xFF5B5891))
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.size - 1

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Top bar with skip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onFinished) {
                    Text(
                        stringResource(R.string.onboarding_skip),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                PageContent(pages[page])
            }

            // Bottom controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == pagerState.currentPage) 10.dp else 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == pagerState.currentPage)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outlineVariant
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Button
                Button(
                    onClick = {
                        if (isLastPage) {
                            onFinished()
                        } else {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (isLastPage) {
                        Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.onboarding_get_started), fontSize = 15.sp)
                    } else {
                        Text(stringResource(R.string.onboarding_next), fontSize = 15.sp)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(page.accentColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = page.emoji, fontSize = 48.sp)
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = stringResource(page.titleRes),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(page.descriptionRes),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )
    }
}
