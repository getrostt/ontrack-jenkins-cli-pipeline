import net.nemerosa.ontrack.jenkins.pipeline.utils.JenkinsUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.cli.Cli

def call(Map<String, ?> params = [:]) {

    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME as String)
    String branch = ParamUtils.getParam(params, "branch", env.ONTRACK_BRANCH_NAME as String)
    String build = ParamUtils.getParam(params, "build", env.ONTRACK_BUILD_NAME as String)
    String stamp = ParamUtils.getParam(params, "stamp")
    String status = ParamUtils.getConditionalParam(params, "status", false, null)
    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)

    Closure logger = {}
    if (logging) {
        logger = {
            println("[ontrack-cli-validate] $it")
        }
    }

    List<String> args = ['validate', '--project', project, '--branch', branch, '--build', build, '--validation', stamp]

    // Computing the status if needed
    String actualStatus = status
    if (!actualStatus) {
        logger("No status is provided, computing status...")
        actualStatus = JenkinsUtils.getValidationRunStatusFromStage(this)
        logger("Computed status: $actualStatus")
    }

    // Setting up the status
    if (actualStatus) {
        args += '--status'
        args += actualStatus
    }

    Cli.call(this, logger, args)

}
