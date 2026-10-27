# Releasing yaml-rule 4.0.0

The 4.0.0 candidate moves the Java API to Jackson 3.2.3 and requires Java 25.
It is incompatible with Jackson 2 callers and changes accepted YAML scalar
values. Read the migration tables in the README before adopting it. The
published 3.0.1 coordinate remains the Jackson 2 line.

## Preparation and review

1. Review the candidate POM and changelog. Check Maven Central and remote tags
   to ensure `4.0.0` has not already been published or tagged. Never overwrite
   a published coordinate or move a release tag.
2. Run the full build on Java 25: `mvn -B -ntp clean verify`.
   Inspect Surefire reports and the main, sources and Javadoc JARs in `target/`.
3. Inspect the dependency tree for SNAPSHOT dependencies and Jackson 2 core,
   databind, datatype, module or dataformat artifacts. Jackson annotations still
   use `com.fasterxml.jackson.core:jackson-annotations` and are expected.
4. Validate release-profile packaging without signing or publishing:
   `mvn -B -ntp -DperformRelease=true -Dgpg.skip=true clean verify`.
   This is an unsigned preflight, not signed-artifact qualification.
5. Merge only after review and green current-head CI. Set the changelog release
   date to the actual publication date and verify the final release commit.

## Publication (separate maintainer action)

Publication requires the maintainer's Central credentials and signing key.
Keep those in Maven/GPG configuration; do not put them in this repository.
Run against the reviewed release commit on a clean checkout.

First run `mvn -B -ntp -DperformRelease=true clean verify` to build, test and sign
the candidate without deploying. Verify the attached signatures and artifacts.
Then, after explicit release approval, run
`mvn -B -ntp -DperformRelease=true deploy`.

The release profile sets `autoPublish=true`: deploy uploads to Central and may
publish automatically after Central validation. It is not a staging-only check.
Do not use `deploy` during preparation. If a deployment times out, inspect its
Central status before retrying or creating a new release.

After Central confirms publication, verify the POM, main JAR, sources, Javadoc
and signatures at `com.networknt:yaml-rule:4.0.0`. Create an annotated `4.0.0`
tag at the exact published source commit and push it as a separate release
action. Record the commit, build results, deployment status and tag.

## After publication

- Move master to `4.0.1-SNAPSHOT` in a follow-up change; keep the tag at 4.0.0.
- Update light-4j and yaml-rule-plugin to depend on released yaml-rule 4.0.0,
  then validate consumers before their coordinated framework release.
- SHA-pinned source-build CI continues to use its existing baseline until an
  intentional promotion refresh. A Maven version edit does not change those
  source pins or qualify a new stack.
