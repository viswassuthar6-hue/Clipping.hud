package com.example.data

import com.example.model.*

object SampleDataProvider {

    fun createInitialProjects(): List<Project> {
        val now = System.currentTimeMillis()
        return listOf(
            Project(
                id = "proj_01",
                title = "Startup Masterclass: Scaling to $10M ARR",
                sourceUrl = "https://storage.googleapis.com/demo-videos/startup-masterclass.mp4",
                sourceType = VideoSourceType.UPLOAD,
                durationSeconds = 1420f, // ~23 mins
                fileSizeMb = 312.4f,
                resolution = "1080p",
                fps = 30,
                status = ProjectStatus.COMPLETED,
                language = "English",
                trimmedStartSec = 120f,
                trimmedEndSec = 780f,
                isTrimmedSaved = true,
                clipCountTarget = 5,
                createdAt = now - (3600 * 1000 * 24 * 2),
                thumbnailPlaceholderColor = 0xFF0D9488
            ),
            Project(
                id = "proj_02",
                title = "The Hidden Economics of Creator Algorithms",
                sourceUrl = "https://youtube.com/watch?v=sample-algorithm-breakdown",
                sourceType = VideoSourceType.YOUTUBE_URL,
                durationSeconds = 860f, // ~14 mins
                fileSizeMb = 185.0f,
                resolution = "1080p",
                fps = 60,
                status = ProjectStatus.COMPLETED,
                language = "English",
                trimmedStartSec = 45f,
                trimmedEndSec = 600f,
                isTrimmedSaved = false,
                clipCountTarget = 4,
                createdAt = now - (3600 * 1000 * 8),
                thumbnailPlaceholderColor = 0xFF7C3AED
            ),
            Project(
                id = "proj_03",
                title = "Deep Dive: Real-Time Multimodal AI in Android",
                sourceUrl = "https://storage.googleapis.com/demo-videos/ai-android-talk.mp4",
                sourceType = VideoSourceType.UPLOAD,
                durationSeconds = 2100f, // 35 mins
                fileSizeMb = 640.2f,
                resolution = "4K",
                fps = 60,
                status = ProjectStatus.COMPLETED,
                language = "English",
                trimmedStartSec = 180f,
                trimmedEndSec = 1200f,
                isTrimmedSaved = true,
                clipCountTarget = 6,
                createdAt = now - (3600 * 1000 * 4),
                thumbnailPlaceholderColor = 0xFF2563EB
            )
        )
    }

    fun createInitialClips(): List<ViralClip> {
        return listOf(
            ViralClip(
                id = "clip_01",
                projectId = "proj_01",
                startSec = 135f,
                endSec = 178f,
                topic = "Business Strategy",
                reasonForSelection = "High-energy contrarian opening debunking vanity metrics, backed by specific revenue statistics",
                transcript = "Most founders obsess over follower counts and impressions. But vanity metrics will bankrupt you. What actually matters is net dollar retention and unit economics from day one. When we pivoted our focus from viral stunts to customer retention, our revenue skyrocketed 400% in six months.",
                emotion = "Determination & Shock",
                scoreBreakdown = ScoreBreakdown(
                    hookScore = 96,
                    emotionalScore = 88,
                    infoScore = 95,
                    entertainmentScore = 86,
                    curiosityScore = 92,
                    storyScore = 90,
                    visualScore = 85,
                    audioScore = 94
                ),
                keywords = listOf("vanity metrics", "retention", "revenue", "skyrocketed 400%", "unit economics"),
                titleOptions = listOf(
                    TitleHookOption(
                        title = "Why Vanity Metrics Will Bankrupt You",
                        hook = "Stop obsessing over followers—this one metric is why 90% of startups go broke.",
                        hookType = HookType.HIGH_STAKES,
                        explanation = "High-urgency framing that warns creators and founders against a fatal mistake."
                    ),
                    TitleHookOption(
                        title = "The 400% Revenue Pivot Nobody Mentions",
                        hook = "We ignored 100,000 views and did this instead... revenue jumped 4x.",
                        hookType = HookType.CURIOSITY_GAP,
                        explanation = "Creates a huge gap between conventional wisdom and shocking results."
                    ),
                    TitleHookOption(
                        title = "The Day One Retention Blueprint",
                        hook = "If you want real cash flow, here's the exact metric top founders watch every morning.",
                        hookType = HookType.ACTIONABLE_SECRET,
                        explanation = "Promises an executive secret formula that viewers can implement immediately."
                    )
                ),
                captionWords = listOf(
                    CaptionWord("Most", 135.0f, 135.3f),
                    CaptionWord("founders", 135.3f, 135.7f, isKeyword = true),
                    CaptionWord("obsess", 135.7f, 136.1f),
                    CaptionWord("over", 136.1f, 136.3f),
                    CaptionWord("follower", 136.3f, 136.8f),
                    CaptionWord("counts", 136.8f, 137.2f),
                    CaptionWord("and", 137.2f, 137.4f),
                    CaptionWord("impressions.", 137.4f, 138.0f),
                    CaptionWord("But", 138.2f, 138.4f),
                    CaptionWord("VANITY", 138.4f, 138.9f, isKeyword = true, suggestedEmoji = "🚨"),
                    CaptionWord("METRICS", 138.9f, 139.5f, isKeyword = true),
                    CaptionWord("will", 139.5f, 139.8f),
                    CaptionWord("bankrupt", 139.8f, 140.4f, isKeyword = true, suggestedEmoji = "💸"),
                    CaptionWord("you!", 140.4f, 140.8f),
                    CaptionWord("What", 141.2f, 141.5f),
                    CaptionWord("actually", 141.5f, 141.9f),
                    CaptionWord("matters", 141.9f, 142.3f),
                    CaptionWord("is", 142.3f, 142.5f),
                    CaptionWord("RETENTION", 142.5f, 143.2f, isKeyword = true, suggestedEmoji = "🔒"),
                    CaptionWord("and", 143.2f, 143.4f),
                    CaptionWord("unit", 143.4f, 143.8f),
                    CaptionWord("economics.", 143.8f, 144.5f, isKeyword = true),
                    CaptionWord("Our", 145.0f, 145.3f),
                    CaptionWord("REVENUE", 145.3f, 145.9f, isKeyword = true, suggestedEmoji = "💰"),
                    CaptionWord("skyrocketed", 145.9f, 146.7f, isKeyword = true, suggestedEmoji = "🚀"),
                    CaptionWord("400%", 146.7f, 147.4f, isKeyword = true, suggestedEmoji = "📈"),
                    CaptionWord("in", 147.4f, 147.6f),
                    CaptionWord("six", 147.6f, 147.9f),
                    CaptionWord("months.", 147.9f, 148.5f)
                ),
                activeStyle = ClipStyle.MODERN,
                activeAspectRatio = AspectRatioType.RATIO_9_16,
                backgroundMode = BackgroundMode.SMART_CROP
            ),
            ViralClip(
                id = "clip_02",
                projectId = "proj_01",
                startSec = 310f,
                endSec = 352f,
                topic = "Hiring & Leadership",
                reasonForSelection = "Punchy one-liner rule followed by a painful past mistake that creates strong empathy",
                transcript = "Never hire someone who needs to be motivated. The best operators arrive with their own fire. When I hired managers to push uninspired employees, it drained our company culture. Hire people obsessed with the craft, not people waiting for orders.",
                emotion = "Inspirational Authority",
                scoreBreakdown = ScoreBreakdown(
                    hookScore = 93,
                    emotionalScore = 91,
                    infoScore = 89,
                    entertainmentScore = 84,
                    curiosityScore = 88,
                    storyScore = 92,
                    visualScore = 82,
                    audioScore = 91
                ),
                keywords = listOf("hire", "motivated", "fire", "culture", "craft"),
                titleOptions = listOf(
                    TitleHookOption(
                        title = "The Golden Rule of Hiring A-Players",
                        hook = "Never hire someone who needs you to motivate them. Here's why.",
                        hookType = HookType.CONTRARIAN,
                        explanation = "Challenges standard HR clichés and positions the speaker as an authentic leader."
                    ),
                    TitleHookOption(
                        title = "Why Managing Low Energy Costs Millions",
                        hook = "The single biggest mistake that almost destroyed our company culture.",
                        hookType = HookType.HIGH_STAKES,
                        explanation = "Evokes emotional dread and curiosity about executive blunders."
                    ),
                    TitleHookOption(
                        title = "How to Spot People with 'Their Own Fire'",
                        hook = "Before you hire your next team member, test for this exact trait.",
                        hookType = HookType.ACTIONABLE_SECRET,
                        explanation = "Direct practical advice that viewers can use in interviews."
                    )
                ),
                captionWords = listOf(
                    CaptionWord("Never", 310.0f, 310.4f, isKeyword = true, suggestedEmoji = "⛔"),
                    CaptionWord("hire", 310.4f, 310.8f),
                    CaptionWord("someone", 310.8f, 311.2f),
                    CaptionWord("who", 311.2f, 311.4f),
                    CaptionWord("needs", 311.4f, 311.7f),
                    CaptionWord("to", 311.7f, 311.9f),
                    CaptionWord("be", 311.9f, 312.1f),
                    CaptionWord("MOTIVATED.", 312.1f, 312.9f, isKeyword = true),
                    CaptionWord("The", 313.2f, 313.4f),
                    CaptionWord("best", 313.4f, 313.7f),
                    CaptionWord("operators", 313.7f, 314.3f, isKeyword = true, suggestedEmoji = "⚡"),
                    CaptionWord("arrive", 314.3f, 314.7f),
                    CaptionWord("with", 314.7f, 314.9f),
                    CaptionWord("their", 314.9f, 315.1f),
                    CaptionWord("OWN", 315.1f, 315.5f, isKeyword = true),
                    CaptionWord("FIRE.", 315.5f, 316.2f, isKeyword = true, suggestedEmoji = "🔥")
                ),
                activeStyle = ClipStyle.BOLD,
                activeAspectRatio = AspectRatioType.RATIO_9_16,
                backgroundMode = BackgroundMode.BLUR
            ),
            ViralClip(
                id = "clip_03",
                projectId = "proj_02",
                startSec = 62f,
                endSec = 104f,
                topic = "Algorithm Secrets",
                reasonForSelection = "Reveals an insider mechanic on short-form video retention graphs that immediately intrigues content creators",
                transcript = "The algorithm doesn't care how beautiful your video is. It measures one thing in the first 3 seconds: scroll-stop latency. If a user hesitates for even 400 milliseconds, the watch probability leaps by 67%. That's why visual pattern interrupts are essential.",
                emotion = "Curiosity & Revelation",
                scoreBreakdown = ScoreBreakdown(
                    hookScore = 98,
                    emotionalScore = 85,
                    infoScore = 96,
                    entertainmentScore = 90,
                    curiosityScore = 97,
                    storyScore = 86,
                    visualScore = 88,
                    audioScore = 92
                ),
                keywords = listOf("algorithm", "scroll-stop", "3 seconds", "pattern interrupts", "watch probability"),
                titleOptions = listOf(
                    TitleHookOption(
                        title = "The 3-Second Algorithm Loophole",
                        hook = "The algorithm only cares about this ONE thing in your first 3 seconds.",
                        hookType = HookType.CURIOSITY_GAP,
                        explanation = "Massive curiosity trigger that targets every short-form creator."
                    ),
                    TitleHookOption(
                        title = "Why Beautiful Videos Still Flop",
                        hook = "Stop wasting hours on cinematic b-roll until you fix this 400ms metric.",
                        hookType = HookType.CONTRARIAN,
                        explanation = "Calls out unnecessary editing effort and redirects focus to pacing."
                    ),
                    TitleHookOption(
                        title = "The Scroll-Stop Pattern Interrupt Hack",
                        hook = "Use this subtle visual trick to boost short-form watch probability by 67%.",
                        hookType = HookType.ACTIONABLE_SECRET,
                        explanation = "Quantifiable promise of growth with actionable immediate technique."
                    )
                ),
                captionWords = listOf(
                    CaptionWord("The", 62.0f, 62.2f),
                    CaptionWord("algorithm", 62.2f, 62.8f, isKeyword = true, suggestedEmoji = "🤖"),
                    CaptionWord("doesn't", 62.8f, 63.2f),
                    CaptionWord("care", 63.2f, 63.5f),
                    CaptionWord("how", 63.5f, 63.7f),
                    CaptionWord("beautiful", 63.7f, 64.3f),
                    CaptionWord("your", 64.3f, 64.5f),
                    CaptionWord("video", 64.5f, 64.9f),
                    CaptionWord("is.", 64.9f, 65.2f),
                    CaptionWord("It", 65.5f, 65.7f),
                    CaptionWord("measures", 65.7f, 66.2f),
                    CaptionWord("ONE", 66.2f, 66.6f, isKeyword = true),
                    CaptionWord("THING", 66.6f, 67.1f, isKeyword = true, suggestedEmoji = "🎯"),
                    CaptionWord("in", 67.1f, 67.3f),
                    CaptionWord("the", 67.3f, 67.5f),
                    CaptionWord("first", 67.5f, 67.8f),
                    CaptionWord("THREE", 67.8f, 68.3f, isKeyword = true),
                    CaptionWord("SECONDS.", 68.3f, 69.1f, isKeyword = true, suggestedEmoji = "⏱️")
                ),
                activeStyle = ClipStyle.HIGH_ENERGY,
                activeAspectRatio = AspectRatioType.RATIO_9_16,
                backgroundMode = BackgroundMode.SMART_CROP
            )
        )
    }

    fun createInitialTransactions(): List<CreditTransaction> {
        val now = System.currentTimeMillis()
        return listOf(
            CreditTransaction(
                id = "tx_01",
                timestamp = now - (3600 * 1000 * 24 * 5),
                amount = 200,
                transactionType = TransactionType.SIGNUP_BONUS,
                projectId = null,
                projectTitle = null,
                balanceAfter = 200,
                description = "Welcome to ViralClip Studio! Initial creator credits balance"
            ),
            CreditTransaction(
                id = "tx_02",
                timestamp = now - (3600 * 1000 * 24 * 2),
                amount = -4,
                transactionType = TransactionType.VIDEO_PROCESSING,
                projectId = "proj_01",
                projectTitle = "Startup Masterclass: Scaling to $10M ARR",
                balanceAfter = 196,
                description = "Processed 11.0 minutes of trimmed video for 5 viral clips"
            ),
            CreditTransaction(
                id = "tx_03",
                timestamp = now - (3600 * 1000 * 8),
                amount = -4,
                transactionType = TransactionType.VIDEO_PROCESSING,
                projectId = "proj_02",
                projectTitle = "The Hidden Economics of Creator Algorithms",
                balanceAfter = 192,
                description = "Processed 9.25 minutes of video for 4 viral clips"
            ),
            CreditTransaction(
                id = "tx_04",
                timestamp = now - (3600 * 1000 * 4),
                amount = -6,
                transactionType = TransactionType.VIDEO_PROCESSING,
                projectId = "proj_03",
                projectTitle = "Deep Dive: Real-Time Multimodal AI in Android",
                balanceAfter = 186,
                description = "Processed 17.0 minutes of trimmed source video for 6 viral clips"
            )
        )
    }

    fun createInitialVipCodes(): List<VipCode> {
        return listOf(
            VipCode(
                code = "VIRAL100K",
                rewardCredits = 100_000,
                maxRedemptions = 10,
                currentRedemptions = 0,
                isActive = true
            ),
            VipCode(
                code = "CREATORVIP",
                rewardCredits = 5_000,
                maxRedemptions = 50,
                currentRedemptions = 3,
                isActive = true
            ),
            VipCode(
                code = "STUDIOPRO",
                rewardCredits = 25_000,
                maxRedemptions = 25,
                currentRedemptions = 1,
                isActive = true
            )
        )
    }
}
