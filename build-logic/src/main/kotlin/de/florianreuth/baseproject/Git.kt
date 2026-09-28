package de.florianreuth.baseproject

import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/**
 * Short hash of the latest commit, or "unknown" if Git is unavailable.
 */
fun Project.latestCommitHash(): String = runGitCommand(listOf("rev-parse", "--short", "HEAD"))

/**
 * Message of the latest commit, or "unknown" if Git is unavailable.
 */
fun Project.latestCommitMessage(): String = runGitCommand(listOf("log", "-1", "--pretty=%B"))

/**
 * Name of the current branch, or "unknown" if Git is unavailable.
 */
fun Project.branchName(): String = runGitCommand(listOf("rev-parse", "--abbrev-ref", "HEAD"))

/**
 * Runs a Git command through a configuration-cache compatible [ValueSource].
 *
 * @param args the arguments passed to `git`
 * @return the trimmed output, or "unknown" if the command fails or prints nothing
 */
fun Project.runGitCommand(args: List<String>): String {
    return providers.of(GitCommand::class.java) { parameters.args.set(args) }.getOrNull() ?: "unknown"
}

abstract class GitCommand : ValueSource<String, GitCommand.GitCommandParameters> {

    @get:Inject
    abstract val execOperations: ExecOperations

    interface GitCommandParameters : ValueSourceParameters {
        val args: ListProperty<String>
    }

    override fun obtain(): String? {
        try {
            val output = ByteArrayOutputStream()
            execOperations.exec {
                commandLine = listOf("git") + parameters.args.get()
                standardOutput = output
                isIgnoreExitValue = true
            }

            return output.toString(Charsets.UTF_8).trim().takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            return null
        }
    }
}
