# Contributing to ViaFabricPlus

Thanks for your interest in contributing! This guide will walk you through setting up a workspace, updating
translations, adding features, and maintaining high-quality contributions.

---

## Setting Up a Workspace

ViaFabricPlus uses **Gradle**. Make sure you have it
installed: [Gradle Installation Guide](https://gradle.org/install/).

1. Clone the repo:

   ```bash
   git clone https://github.com/ViaVersion/ViaFabricPlus
   ```
2. Enter the project folder:

   ```bash
   cd ViaFabricPlus
   ```
3. Generate sources:

   ```bash
   ./gradlew genSources
   ```
4. Open the project as a **Gradle project** in your preferred IDE.
5. Run the mod

---

## Updating Translation Files

If you want to help translating ViaFabricPlus you can do so on [Crowdin](https://crowdin.com/project/viafabricplus).

---

## Adding a New Feature or Fixing a Bug

1. Create a branch (e.g. `feature/fix-xyz` or `fix/fix-xyz`).
2. Implement and test your changes thoroughly.
3. Write clean, readable code (descriptive names, no clutter).
4. Follow [Google's Java Code Style](https://google.github.io/styleguide/javaguide.html).
5. If you modify the API:
    - Update documentation in `docs/`
    - Add Javadocs to your code
    - Avoid breaking backwards compatibility unless absolutely necessary
6. Open a pull request and wait for review.

---

## Adding Protocol Fixes

Protocol fixes are the **heart of ViaFabricPlus**. They're what make this project unique, so it's important to add only
relevant and correct changes.

Guidelines for fixes:

- Only add fixes that **affect gameplay or server communication**
- Avoid purely **visual-only tweaks**
- If unsure, ask in the [ViaVersion Discord](https://discord.gg/viaversion)
- Most useful fixes usually involve **movement or networking**

Proof is required:

- Show that your fix matches real game changes
- Provide a source diff (if available)

Remember: fixes should reflect actual **historical behavior**, not cosmetic adjustments.

---

## Maintaining the Mod

For details on ongoing development, see [MAINTAINING.md](docs/MAINTAINING.md).
