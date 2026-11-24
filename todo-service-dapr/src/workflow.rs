///
/// @package Showcase-Workflow-Engines
///
/// @file Workflow handlers
/// @copyright 2026-present Christoph Kappel <christoph@unexist.dev>
/// @version $Id$
///
/// This program can be distributed under the terms of the GNU GPLv2.
/// See the file LICENSE for details.
///
use std::{
    sync::atomic::{AtomicI32, Ordering},
    time::Duration,
};

use dapr::workflow::{
    ActivityContext, ActivityContextExt, ActivityOptions, RegistryExt, RetryPolicy, WorkflowClient,
    WorkflowContext, WorkflowContextExt, WorkflowError,
};

const INSTANCE_ID: &str = "a7a4168d-3a1c-41da-8a4f-e7f6d9c718d9";
pub const TEST_WORKFLOW: &str = "TestWorkflow";
pub const TEST_ACTIVITY: &str = "TestActivity";
pub const FAIL_ACTIVITY: &str = "FailActivity";
pub const TEST_EVENT: &str = "testEvent";

static STAGE: AtomicI32 = AtomicI32::new(0);
static FAIL_ACTIVITY_TRIES: AtomicI32 = AtomicI32::new(0);

pub async fn test_workflow(ctx: WorkflowContext) -> dapr::workflow::Result<Option<String>> {
    let input: i32 = ctx.get_input_typed()?;
    let _: String = ctx.call_activity_typed(TEST_ACTIVITY, input).await?;

    let event_payload: String = ctx
        .wait_for_external_event_typed(TEST_EVENT, Some(Duration::from_secs(60)))
        .await?;

    if !ctx.is_replaying() {
        println!("workflow received event payload: {event_payload}");
    }

    let output: String = ctx.call_activity_typed(TEST_ACTIVITY, input).await?;

    let retry_policy = RetryPolicy::new(3, Duration::from_millis(100))
        .with_backoff_coefficient(2.0)
        .with_max_retry_interval(Duration::from_secs(1));

    let fail_result = ctx
        .call_activity_with_options_typed::<String>(
            FAIL_ACTIVITY,
            ActivityOptions::new().with_retry_policy(retry_policy),
        )
        .await;

    if fail_result.is_ok() {
        return Err(WorkflowError::Other(
            "unexpected no error executing fail activity".to_string(),
        ));
    }

    Ok(Some(serde_json::to_string(&output)?))
}

pub async fn test_activity(
    ctx: ActivityContext,
    input: Option<String>,
) -> dapr::workflow::Result<Option<String>> {
    let input: i32 = ctx.get_input(input.as_deref())?;
    let stage = STAGE.fetch_add(input, Ordering::SeqCst) + input;

    Ok(Some(serde_json::to_string(&format!("Stage: {stage}"))?))
}

pub async fn fail_activity(
    _ctx: ActivityContext,
    _input: Option<String>,
) -> dapr::workflow::Result<Option<String>> {
    FAIL_ACTIVITY_TRIES.fetch_add(1, Ordering::SeqCst);

    Err(WorkflowError::Other("Dummy activity error".to_string()))
}

pub async fn create_client() -> impl Future<Output = anyhow::Result<WorkflowClient>> {
    async {
        let mut workflow_client = WorkflowClient::new().await?;

        workflow_client
            .registry_mut()
            .add_workflow(TEST_WORKFLOW, test_workflow);

        workflow_client
            .registry_mut()
            .add_activity(TEST_ACTIVITY, test_activity);

        workflow_client
            .registry_mut()
            .add_activity(FAIL_ACTIVITY, fail_activity);

        Ok(workflow_client)
    }
}
