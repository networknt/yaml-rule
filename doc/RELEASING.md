# Releasing yaml-rule 4.0.0

The planned 4.0.0 release moves the Java API to Jackson 3.2.3 and requires
Java 25. It is incompatible with Jackson 2 callers and changes accepted YAML
scalar values. Read the README migration tables before adopting it. Published
3.0.1 remains the Jackson 2 line.

Master must stay at `4.0.0-SNAPSHOT` while source-built consumers request that
coordinate. The release-only version, date and SCM tag edits belong on a
separate release branch, not master. An untagged branch is not a published release.

## Prepare the release branch

1. Check Maven Central and remote tags to ensure 4.0.0 has not already been
   published or tagged. Never overwrite a published coordinate or move a tag.
2. From the reviewed, clean master commit, create `release/4.0.0`. On that branch
   only, set the project version to `4.0.0` and `scm/tag` to `4.0.0`. Move the
   changelog's Unreleased entries into a 4.0.0 section with the intended
   publication date. Commit all three edits together. If the release date changes,
   update it in a new release-branch commit and repeat review and verification.
3. Review the exact release-branch commit and require green CI on that SHA.
   The test workflow runs for `release/**` pushes as well as master and PRs.
   Do not merge the release-only edits back into master.
4. On Java 25, run `mvn -B -ntp clean verify`. Inspect Surefire reports and the
   main, sources and Javadoc JARs. Inspect the dependency tree for SNAPSHOTs and
   Jackson 2 core, databind, datatype, module or dataformat artifacts. Jackson
   annotations still use `com.fasterxml.jackson.core:jackson-annotations` and
   are expected.
5. Validate release-profile packaging without signing or publishing:
   `mvn -B -ntp -DperformRelease=true -Dgpg.skip=true clean verify`.
   This is an unsigned rehearsal. Its files are not claimed to be the bytes
   eventually published; the approved publication invocation rebuilds them.

## Publish the reviewed release commit

Publication requires the maintainer's Central credentials and signing key in
Maven/GPG configuration, never in this repository. After explicit release approval,
use a clean checkout of the exact reviewed release-branch SHA. Confirm that its
version is 4.0.0, SCM tag is 4.0.0, changelog date is correct, and no credentials
or skip flags override signing or tests.

Run one Maven lifecycle invocation:

```sh
mvn -B -ntp -DperformRelease=true clean deploy
```

This invocation rebuilds, tests, packages, signs during verify, and deploys that
same lifecycle's attached artifacts. Do not split this into a signed `verify`
followed by a separate `deploy` and claim artifact identity across those builds.
A successful unsigned rehearsal does not prove signing credentials or upload.

The existing profile has `autoPublish=true`: deploy uploads to Central and may
publish automatically after Central validation. It is not a staging-only check.
Do not run deploy during preparation. If it times out, inspect the Central
operation before retrying or attempting another release.

Retain the successful deployment log and the generated POM, main JAR, sources,
Javadoc, signatures and SHA-256 hashes from that invocation. After Central confirms
publication, download the published artifacts, compare them with the retained
files and verify their signatures. Do not rebuild to obtain the comparison files.
Then create an annotated `4.0.0` tag at that exact source SHA and push it as a
separate maintainer action. Record the SHA, results, deployment status and tag.
If publication fails, do not tag it as released.

## Coordinate consumers after publication

- Update light-4j, light-rest-4j, yaml-rule-plugin, light-gateway and http-sidecar
  to released yaml-rule 4.0.0 through reviewed adoption changes and validate them.
- Keep master's existing snapshot coordinate until the source-build consumers
  and tooling are ready for a coordinated next-development-version change.
  `4.0.1-SNAPSHOT` is the proposed next version, not an automatic isolated bump.
- SHA-pinned CI keeps its existing baseline until an intentional promotion
  refresh. A Maven version edit does not update source pins or qualify a new stack.
