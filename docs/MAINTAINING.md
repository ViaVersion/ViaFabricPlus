# Updating Instructions

These are the usual steps for updating **ViaFabricPlus** to a new Minecraft version.
If you're unsure about anything, feel free to ask in the [ViaVersion Discord](https://discord.gg/viaversion).

---

## 1. Update Dependencies

Set `updating_minecraft = true`.

Update all upstream versions in `gradle.properties`. The main ones are:

- `minecraft_version`
- `yarn_mappings_version`
- `fabric_loader_version`
- `fabric_api_version`
- `supported_minecraft_versions` (if needed)

Also update versions in the `dependencies` block of `build.gradle.kts`.

---

## 2. Update Core References

- Update the `NATIVE_VERSION` field in `ProtocolTranslator`
- Update protocol constants in `ViaFabricPlusProtocol`
- Update `ItemTranslator#getClientboundItemType` if a new item type was added

---

## 3. Port the Code

1. Decompile the Minecraft source with your preferred tool.
2. Try to compile the mod and port the code until all fixes work again.

---

## 4. Update Data Diffs

Run `gradle test` to automatically update various data jsons in the assets.

Manually check the following files:
- `entity-dimensions.json`

---

## 5. Validate Mixins

Check if all mixins in the `injection` package still apply correctly.
Critical ones include:

- `MixinClientWorld#tickEntity`
- `MixinClientWorld#tickPassenger`
- `MixinPlayer#getBlockBreakingSpeed`

---

## 6. Diff Game Code

Diff the new Minecraft source against the previous version (e.g. using `git`).
Implement all relevant changes for ViaFabricPlus. These usually include:

- **Logic changes** (e.g. `if (a && b)` → `if (b || a)`)
- **Movement changes** (e.g. `player.yaw` → `player.headYaw`)
- **Networking changes** (e.g. new packet or changed structure)
- **Visual changes** (e.g. animations)

Note: ViaVersion already covers most server-side gameplay changes.
ViaFabricPlus usually handles **client-side and deeper integration fixes**.

See [Contributing Guidelines](../CONTRIBUTING.md#Adding-Protocol-Fixes) for details on which fixes matter.

---

## 7. Focus Areas in Game Code

**Usually important (mojang mappings):**

- `net.minecraft`
- `net.minecraft.client`
    - `gui`
    - `multiplayer`
    - `player`
- `net.minecraft.util`
- `net.minecraft.world`
    - `entity`
    - `inventory`
    - `item`
    - `level` (including `block`)

**Usually safe to skip:**

- `com.mojang`
- `net.minecraft.advancements`
- `net.minecraft.commands`
- `net.minecraft.data`
- `net.minecraft.gametest`
- `net.minecraft.realms`
- `net.minecraft.recipebook`
- `net.minecraft.references`
- `net.minecraft.resources`
- `net.minecraft.server`
- `net.minecraft.sounds`
- `net.minecraft.stats`
- `net.minecraft.tags`

---

## 8. Verify Upstream

Check the ViaVersion/upstream protocol implementation.

- Report upstream issues when needed
- If an issue can’t be fixed upstream without excessive work, add a client-side workaround in ViaFabricPlus

---

## 9. Final Steps

1. Run the game and verify **all GUIs and visuals**.
2. Clean up your code and ensure it’s readable.

    * Client-side fixes are sorted by protocol version, newest at the top.
3. Set `updating_minecraft = false`.
4. Open a pull request and wait for review.

---

# Project Structure

- Every change to the game is called a **feature**.
- Features live under both `features/` and `injection/mixin/features/`.
- Loading is handled by static blocks and a dummy `init` function in `FeaturesLoading`.

---

# Build Files

- Common build logic comes from the [BaseProject Gradle convention plugin](https://github.com/FlorianMichael/BaseProject).
- The root project includes all submodules (including optional ones like `viafabricplus-visuals`).
- Be careful not to introduce unintended dependencies on optional submodules.

---

# Release Process

1. Set `project_version` in `gradle.properties` to the new release version.
2. Pin version IDs of `configureVVDependencies` in `build.gradle.kts`.
3. Commit with message:

   ```
   <version> Release
   ```

## Versioning Scheme

- **Major** → Breaking or fundamental refactors (e.g. new mappings, full rewrite)
- **Minor** → Port to a new game version or major feature addition
- **Patch** → Bug fixes or small features

After releasing:

- Switch back to `-SNAPSHOT` version
- Unpin `configureVVDependencies`

If the next commit would be merging someone else’s PR, make a commit like:

```
Bump version to <version>
```

---

# Git Branches

- `main` → all new changes are merged here
- After the last release for a version, a branch is created with that version name (e.g. `1.21.5`)

See [ViaFabricPlus-archive](https://github.com/FlorianMichael/ViaFabricPlus-archive) for older branches.

---

# Backports

- Releases for older versions are **backports**.
- Their version string should end with `-BACKPORT`.
- Backports are pushed to the branch matching their Minecraft version.
