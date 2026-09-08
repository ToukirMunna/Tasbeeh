# 🚀 The Ultimate Google Antigravity (AGY) Master Guide
> *The Complete Playbook: Architecture, Mental Models, Modalities, Model Strategies, Slash Commands, Subagents, and Pro Tips.*

---

## 📑 Table of Contents
1. [What Makes Antigravity Different?](#1-what-makes-antigravity-different)
2. [The 3 Core Modalities & Workflow Fit](#2-the-3-core-modalities--workflow-fit)
3. [Model Selection & Intelligence Strategy](#3-model-selection--intelligence-strategy)
4. [Slash Commands Master Reference](#4-slash-commands-master-reference)
5. [Context Pinning & @ Mentions](#5-context-pinning---mentions)
6. [Subagent Swarms & Multi-Agent Delegation](#6-subagent-swarms--multi-agent-delegation)
7. [The Customization Architecture (Rules, Skills, MCP, Hooks)](#7-the-customization-architecture-rules-skills-mcp-hooks)
8. [The 10x Developer Playbook: Pro Tips & Best Practices](#8-the-10x-developer-playbook-pro-tips--best-practices)
9. [Quick-Reference Cheat Sheet & Keyboard Shortcuts](#9-quick-reference-cheat-sheet--keyboard-shortcuts)

---

## 1. What Makes Antigravity Different?

Google Antigravity (AGY) is not a simple chat interface—it is an **agentic pair-programming operating system**. Instead of only producing code snippets for you to copy-paste, Antigravity executes an autonomous **Sense $\rightarrow$ Plan $\rightarrow$ Act $\rightarrow$ Verify** loop:

* **Autonomous Tool Execution**: Reads/writes files, executes terminal builds, runs tests, inspects ADB devices, and searches live docs.
* **Progressive Disclosure**: Keeps its reasoning sharp by loading tools and skills on-demand rather than polluting the prompt context window.
* **Living System Artifacts**: Renders structured design documents, implementation plans, and walkthroughs inside the Auxiliary Pane (`<appDataDir>\brain\<conversation-id>`).

```text
       ┌───────────────────────────────────────────────────────┐
       │                 USER PROMPT / GOAL                    │
       └──────────────────────────┬────────────────────────────┘
                                  ▼
       ┌───────────────────────────────────────────────────────┐
       │                   PLANNING PHASE                      │
       │    • Researches Codebase & Requirements               │
       │    • Generates implementation_plan.md                 │
       │    • Waits for User Alignment                         │
       └──────────────────────────┬────────────────────────────┘
                                  ▼
       ┌───────────────────────────────────────────────────────┐
       │                  EXECUTION PHASE                      │
       │    • Modifies Files (replace_file_content)            │
       │    • Runs CLI Commands & Compiler Checks              │
       │    • Delegates Heavy Research to Subagents            │
       └──────────────────────────┬────────────────────────────┘
                                  ▼
       ┌───────────────────────────────────────────────────────┐
       │                 VERIFICATION & LOGS                   │
       │    • Runs Automated Tests & Warm Builds               │
       │    • Generates walkthrough.md & Appends to LOGS.md    │
       └───────────────────────────────────────────────────────┘
```

---

## 2. The 3 Core Modalities & Workflow Fit

Antigravity operates across three distinct interaction tiers. Knowing when to use which is the key to maximum velocity:

### A. Passive Modality: Antigravity Tab (Autocomplete & Supercomplete)
* **How it works**: Next-intent prediction driven by surrounding code, open tabs, recent terminal outputs, and clipboard.
* **Supercomplete**: Anticipates not just single tokens, but multi-line refactors and deletions in floating inline diffs.
* **Tab to Jump**: Predicts your next editing destination across files and lets you jump instantly by pressing <kbd>Tab</kbd>.
* **Tab to Import**: Automatically adds missing package imports when referencing unimported types.
* **Controls**:
  * Accept Full: <kbd>Tab</kbd>
  * Accept Word-by-Word: <kbd>Ctrl</kbd> + <kbd>→</kbd> (Windows/Linux) or <kbd>Cmd</kbd> + <kbd>→</kbd> (Mac)
  * Dismiss: <kbd>Esc</kbd>

### B. Instructive Modality: Inline Command (<kbd>Ctrl</kbd> + <kbd>I</kbd> / <kbd>Cmd</kbd> + <kbd>I</kbd>)
* **Best for**: Surgical, localized refactorings and targeted function generation.
* **How to use**:
  1. Highlight a block of code (e.g., a function, composable, or SQL query).
  2. Press <kbd>Ctrl</kbd> + <kbd>I</kbd>.
  3. Type your instruction: *"Extract this into a reusable helper"* or *"Add error handling for NetworkException"*.
* **Benefit**: Zero context pollution in your main conversation thread.

### C. Collaborative Modality: Agent Mode & Sidebar Chat
* **Best for**: Feature construction from scratch, complex bug hunting, migrations, and multi-file architecture refactoring.
* **How to use**: Open the Chat Canvas, @mention relevant files, and provide your high-level goal.

---

## 3. Model Selection & Intelligence Strategy

Antigravity gives you granular control over the reasoning budget and model selection:

| Model Tier | Ideal Use Cases | Characteristics |
| :--- | :--- | :--- |
| **Gemini Flash (Low / Default)** | Quick file edits, one-line fixes, unit tests, fast lookups, documentation queries. | Sub-second response time, minimal token consumption, ultra-responsive tool loops. |
| **Gemini Flash (High Reasoning)** | Multi-file features, Compose state management, database migrations, complex debugging. | Balances high-speed tool execution with deep logical verification and edge-case detection. |
| **Gemini Pro** | Architecture design, major algorithmic refactoring, mathematical proofs, large-scale migrations. | Maximum reasoning depth, deep structural awareness across hundreds of files. |

### 💡 Optimal Model Switching Strategy:
1. **Planning Phase**: Use **Gemini Flash (High)** or **Gemini Pro** when drafting the initial `implementation_plan.md` or discussing architectural tradeoffs.
2. **Execution & Build Phase**: Switch to **Gemini Flash** for blistering fast tool execution, rapid file edits, and build verification.

---

## 4. Slash Commands Master Reference

Type `/` in the Chat Canvas to trigger specialized workflows:

| Slash Command | Purpose | When to Use |
| :--- | :--- | :--- |
| **`/goal`** | **Autonomous Goal Mode** | When starting a large task that requires multiple steps, builds, and self-corrections without stopping until 100% done. |
| **`/grill-me`** | **Interactive Requirement Alignment** | When you have a vague feature idea and want the agent to interview you with sharp design questions before writing code. |
| **`/teamwork-preview`** | **Multi-Agent Swarm** | Spawns a team of autonomous subagents to tackle large, decoupled tasks in parallel (e.g., refactoring 10 separate screens). |
| **`/schedule`** | **Cron & Delayed Timers** | Sets a one-time reminder or recurring background job (e.g., check deployment every 10 minutes). |
| **`/browser`** | **Live Browser Testing** | Spins up a headless or visual browser to test web applications, click buttons, and inspect network requests. |
| **`/learn`** | **Persistent Lesson Storage** | Teaches the agent a correction or rule that persists across future conversations and projects. |
| **`/clear`** | **Reset Conversation** | Clears chat memory for a fresh context window. |

---

## 5. Context Pinning & @ Mentions

Type `@` in the chat prompt to inject precise context directly into the agent's attention window:

* **`@file` / `@folder`**: Pins specific files or entire module directories.
* **`@conversations`**: Links previous chat sessions so the agent inherits past architectural decisions.
* **`@terminal`**: Injects live terminal logs, build failure traces, or ADB output.
* **`@rules`**: Explicitly forces activation of specific project rules.
* **`@docs`**: Directly searches authoritative Android/Google developer knowledge bases.

> [!TIP] **The Golden Rule of Context**: Don't dump 50 files into `@`. Pin only the 2–3 root interfaces or data models that matter. Let the agent use `grep_search` and `find_by_name` on demand to keep its context clean.

---

## 6. Subagent Swarms & Multi-Agent Delegation

Antigravity can spawn background subagents that inherit tools, perform heavy work in parallel, and report back when finished:

### Built-in Subagent Types:
1. **`research`**: A read-only subagent that scours the codebase, reads large files, and searches documentation in a separate context without bloating your main chat.
2. **`self`**: A full-powered clone of the main agent that can build, test, and write code in parallel workspaces or branches.

### Communication Pattern:
* You launch subagents via `invoke_subagent`.
* You do **NOT** need to poll in a loop—the messaging system will automatically wake up the parent agent with a notification as soon as the subagent finishes.

---

## 7. The Customization Architecture (Rules, Skills, MCP, Hooks)

Antigravity is fully customizable at both the Global level (`~/.gemini/config/`) and Project level (`.agents/` or `AGENTS.md`):

```text
Customization Priority (Highest to Lowest):
1. Project Workspace: .agents/ or root AGENTS.md / GEMINI.md
2. Declared Configs: skills.json / plugins.json in workspace
3. Global User Config: ~/.gemini/config/
4. Built-in Application Skills
```

### The Customization Toolset:
1. **Rules (`AGENTS.md` / `GEMINI.md`)**:
   * Always-on laws (e.g., 250-line limit, 100% extracted strings, no raw emojis).
2. **Skills (`skills/<name>/SKILL.md`)**:
   * Procedural runbooks loaded on-demand (e.g., `android-cli`, `release-management`).
3. **Plugins (`plugins/<name>/plugin.json`)**:
   * Bundles combining skills, rules, and MCP servers into a single distributable package.
4. **Hooks (`hooks.json`)**:
   * Scripts triggered before or after agent tool calls (e.g., auto-run linter after file edits).
5. **Model Context Protocol (MCP)**:
   * Standardized protocol connecting the agent to local databases, GitHub APIs, Figma, or custom tools via `mcp_config.json`.

---

## 8. The 10x Developer Playbook: Pro Tips & Best Practices

### 🎯 Tip 1: The "Planning Mode" Superpower
Never let an agent blindly generate code across 15 files at once. Force it to create an `implementation_plan.md` artifact first. Review the architecture, verify dependencies, and click **Approve** to execute.

### 🎯 Tip 2: The 250-Line Decomposition Rule
LLMs perform with 99% accuracy when files are under 250 lines. When a file approaches 220 lines, decompose it into `components/` and `domain/engine/`. This keeps the agent's edits localized and completely prevents regression bugs.

### 🎯 Tip 3: Warm Gradle Daemon Execution
Never run cold builds. Spin up the Gradle daemon with `.\gradlew.bat --daemon` in RAM. Builds will execute in 2–4 seconds instead of 40 seconds, enabling the agent to write, build, and verify in rapid micro-cycles.

### 🎯 Tip 4: Zero Emojis & Strict String Tokens
Enforcing string resource extraction (`res/values/strings.xml`) and semantic Material 3 tokens (`MaterialTheme.colorScheme`) keeps your UI 100% decoupled and prevents hallucinated styling.

### 🎯 Tip 5: Using `/grill-me` Before Complex Features
If you have an idea like *"Add offline audio sync with cloud backup"*, run `/grill-me`. The agent will ask 4–5 questions about encryption, conflict resolution, battery impact, and storage limits, saving you hours of refactoring later.

---

## 9. Quick-Reference Cheat Sheet & Keyboard Shortcuts

### ⌨️ Universal Keyboard Shortcuts

| Shortcut | Action | Scope |
| :--- | :--- | :--- |
| <kbd>Tab</kbd> | Accept Full Autocomplete / Supercomplete | Editor |
| <kbd>Ctrl</kbd> + <kbd>→</kbd> | Accept Next Word in Autocomplete | Editor |
| <kbd>Esc</kbd> | Dismiss Autocomplete Suggestion | Editor |
| <kbd>Ctrl</kbd> + <kbd>I</kbd> | Open Inline Code Generator / Refactor | Editor (Selection) |
| <kbd>Ctrl</kbd> + <kbd>L</kbd> | Open / Focus Sidebar Chat | Global |
| <kbd>@</kbd> | Open Context Mention Menu | Chat Canvas |
| <kbd>/</kbd> | Open Slash Commands Menu | Chat Canvas |

---

### 📋 Slash Command Cheatsheet

```text
/goal               -> Unstoppable autonomous long-running execution
/grill-me           -> Requirements interview to align on design decisions
/teamwork-preview   -> Multi-agent parallel task swarm
/schedule           -> Cron & one-shot delayed reminders
/browser            -> Web automation, layout testing, and live scraping
/learn              -> Persist a new lesson/rule to permanent memory
/clear              -> Reset conversation context
```
