package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.UserSession
import com.example.ui.components.CoralPrimaryButton
import com.example.ui.components.TravelGuideLogoMark
import com.example.ui.components.WhiteOutlinedButton
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    session: UserSession,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var progressTarget by remember { mutableFloatStateOf(0.15f) }
    val progress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(durationMillis = 1500),
        label = "splash_progress"
    )

    LaunchedEffect(Unit) {
        progressTarget = 1f
        delay(1750)
        when {
            session.isLoggedIn || session.isGuest -> onNavigateToHome()
            session.hasCompletedOnboarding -> onNavigateToLogin()
            else -> onNavigateToOnboarding()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashGradient)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            TravelGuideLogoMark(size = 76.dp)
            Spacer(Modifier.height(22.dp))
            Text(
                text = "TravelGuide+",
                style = MaterialTheme.typography.displaySmall,
                color = SurfaceWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Explore More. Worry Less.",
                style = MaterialTheme.typography.bodyLarge,
                color = SurfaceWhite.copy(alpha = 0.85f)
            )
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 48.dp, vertical = 48.dp)
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = SurfaceWhite,
            trackColor = SurfaceWhite.copy(alpha = 0.25f)
        )
    }
}

private data class OnboardingPageData(
    val title: String,
    val description: String,
    val imageRes: Int
)

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val pages = remember {
        listOf(
            OnboardingPageData(
                title = "Discover Amazing\nDestinations",
                description = "Explore India and the world with state-wise guides, city picks and local tips.",
                imageRes = R.drawable.img_onboarding_discover
            ),
            OnboardingPageData(
                title = "Plan Smart\nDay-Wise Trips",
                description = "Build custom itineraries with route maps, travel timings and budget estimates.",
                imageRes = R.drawable.img_onboarding_planner
            ),
            OnboardingPageData(
                title = "Verified Taxis,\nStays & Guides",
                description = "Download offline PDF travel guides and book verified drivers, hotels & managers.",
                imageRes = R.drawable.img_onboarding_guides
            )
        )
    }
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onFinished,
                modifier = Modifier.testTag("onboarding_skip_button")
            ) {
                Text("Skip", color = Coral, fontWeight = FontWeight.SemiBold)
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { index ->
            val page = pages[index]
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(310.dp),
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 2.dp
                ) {
                    Image(
                        painter = painterResource(id = page.imageRes),
                        contentDescription = page.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(Modifier.height(28.dp))
                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Navy,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        }

        Row(
            modifier = Modifier.padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pages.size) { i ->
                val active = pagerState.currentPage == i
                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .width(if (active) 28.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (active) Coral else CardBorder)
                )
            }
        }

        CoralPrimaryButton(
            text = if (pagerState.currentPage == pages.lastIndex) "Get Started →" else "Next →",
            testTag = "onboarding_next_button",
            onClick = {
                if (pagerState.currentPage < pages.lastIndex) {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                } else {
                    onFinished()
                }
            }
        )
    }
}

@Composable
fun LoginScreen(
    onSendOtp: (String) -> Unit,
    onGoogleProfileSignIn: (name: String, email: String) -> Unit,
    onEmailSignIn: (name: String, email: String) -> Unit,
    onContinueAsGuest: () -> Unit
) {
    var phone by remember { mutableStateOf("+91 98765 43210") }
    var showEmailDialog by remember { mutableStateOf(false) }
    var showGoogleSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Navy, fontWeight = FontWeight.Bold)) {
                    append("Welcome ")
                }
                withStyle(SpanStyle(color = Coral, fontWeight = FontWeight.Bold)) {
                    append("back")
                }
            },
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Sign in to save trips and buy guides",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )

        Spacer(Modifier.height(24.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = SurfaceWhite,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "MOBILE NUMBER",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Spacer(Modifier.height(6.dp))
                BasicTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    textStyle = MaterialTheme.typography.titleMedium.copy(color = Navy),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_phone_input")
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        CoralPrimaryButton(
            text = "Send OTP",
            testTag = "send_otp_button",
            onClick = { onSendOtp(phone) }
        )

        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = CardBorder)
            Text(
                text = "  or continue with  ",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = CardBorder)
        }

        Spacer(Modifier.height(16.dp))
        WhiteOutlinedButton(
            text = "G  Continue with Google",
            onClick = { showGoogleSheet = true },
            modifier = Modifier.fillMaxWidth(),
            testTag = "google_login_button"
        )
        Spacer(Modifier.height(12.dp))
        WhiteOutlinedButton(
            text = "Email Login",
            icon = Icons.Default.Email,
            onClick = { showEmailDialog = true },
            modifier = Modifier.fillMaxWidth(),
            testTag = "email_login_button"
        )

        Spacer(Modifier.height(16.dp))
        TextButton(
            onClick = onContinueAsGuest,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .testTag("guest_login_button")
        ) {
            Text(
                text = "Continue as Guest",
                style = MaterialTheme.typography.labelLarge,
                color = Coral
            )
        }

        Spacer(Modifier.weight(1f))
        Text(
            text = "By continuing you agree to Terms & Privacy Policy",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (showGoogleSheet) {
        AlertDialog(
            onDismissRequest = { showGoogleSheet = false },
            containerColor = SurfaceWhite,
            title = { Text("Choose local profile", color = Navy) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Local account session (Firebase Google Sign-In disabled by user).",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                showGoogleSheet = false
                                onGoogleProfileSignIn("Ravi Meena", "ravi.meena4795@gmail.com")
                            },
                        color = Lavender,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Ravi Meena", fontWeight = FontWeight.SemiBold, color = Navy)
                            Text("ravi.meena4795@gmail.com", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                showGoogleSheet = false
                                onGoogleProfileSignIn("Amit Verma", "amit@email.com")
                            },
                        color = Lavender,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Amit Verma", fontWeight = FontWeight.SemiBold, color = Navy)
                            Text("amit@email.com", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGoogleSheet = false }) { Text("Close", color = Coral) }
            }
        )
    }

    if (showEmailDialog) {
        var nameInput by remember { mutableStateOf("Amit Verma") }
        var emailInput by remember { mutableStateOf("amit@email.com") }
        AlertDialog(
            onDismissRequest = { showEmailDialog = false },
            containerColor = SurfaceWhite,
            title = { Text("Email Login", color = Navy) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Full Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email Address") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showEmailDialog = false
                        onEmailSignIn(nameInput, emailInput)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Coral)
                ) { Text("Continue") }
            },
            dismissButton = {
                TextButton(onClick = { showEmailDialog = false }) { Text("Cancel", color = Navy) }
            }
        )
    }
}

@Composable
fun OtpVerifyScreen(
    phone: String,
    onBack: () -> Unit,
    onVerified: () -> Unit
) {
    var otpText by remember { mutableStateOf("4829") }
    var countdown by remember { mutableIntStateOf(24) }

    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000)
            countdown--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBack),
                shape = CircleShape,
                color = SurfaceWhite,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Navy)
                }
            }
            Spacer(Modifier.width(12.dp))
            Text("Verify OTP", style = MaterialTheme.typography.headlineSmall, color = Navy)
        }

        Spacer(Modifier.height(14.dp))
        Text(
            text = "Code sent to $phone",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(Modifier.height(22.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            BasicTextField(
                value = otpText,
                onValueChange = { if (it.length <= 5 && it.all { ch -> ch.isDigit() }) otpText = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("otp_input"),
                decorationBox = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (i in 0 until 5) {
                            val digit = otpText.getOrNull(i)?.toString() ?: ""
                            val focused = otpText.length == i
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(58.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = SurfaceWhite,
                                border = BorderStroke(
                                    width = if (focused) 2.dp else 1.dp,
                                    color = if (focused) Coral else CardBorder
                                )
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = digit,
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = Navy,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = if (countdown > 0) "Resend in 00:${countdown.toString().padStart(2, '0')}" else "Resend OTP",
            style = MaterialTheme.typography.bodySmall,
            color = if (countdown > 0) TextMuted else Coral,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(enabled = countdown == 0) { countdown = 24 }
        )

        Spacer(Modifier.height(20.dp))
        CoralPrimaryButton(
            text = "Verify & Continue",
            enabled = otpText.length >= 4,
            testTag = "verify_otp_button",
            onClick = onVerified
        )

        Spacer(Modifier.height(18.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = GreenSoft,
            border = BorderStroke(1.dp, Green.copy(alpha = 0.25f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Green, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "On success, you land on Home. Guests can browse; purchase & trip-saving will ask for an account.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Green
                )
            }
        }
    }
}
