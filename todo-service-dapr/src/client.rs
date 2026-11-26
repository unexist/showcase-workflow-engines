///
/// @package Showcase-Workflow-Engines
///
/// @file Todo client entry
/// @copyright 2026-present Christoph Kappel <christoph@unexist.dev>
/// @version $Id$
///
/// This program can be distributed under the terms of the GNU GPLv2.
/// See the file LICENSE for details.
///
use std::{sync::Arc, time::Duration};

use dapr::{
    client::{ApiTokenInterceptor, ClientOptions},
    dapr::proto::runtime::v1::dapr_client::DaprClient,
    workflow::{ScheduleOptions, WorkerHandle, WorkflowClient},
};

use anyhow::bail;
use tokio::sync::Mutex;

use crate::{todo::Todo, workflow};

type TodoDaprClient = dapr::Client<
    DaprClient<
        tonic::service::interceptor::InterceptedService<
            tonic::transport::channel::Channel,
            ApiTokenInterceptor,
        >,
    >,
>;

pub(crate) struct TodoClient {
    dapr_client: Arc<Mutex<TodoDaprClient>>,
    workflow_client: Arc<Mutex<WorkflowClient>>,
    _worker: Arc<Mutex<WorkerHandle>>,
}

impl TodoClient {
    pub(crate) fn create(uri: &str) -> impl Future<Output = anyhow::Result<Self>> {
        async {
            /* Dapr client */
            let opts = ClientOptions::new()
                .with_address(uri.to_string())
                .with_timeout(Duration::from_secs(10));

            /* Workflow & worker */
            let mut workflow_client = workflow::create_client().await?;
            let worker = workflow_client.start_worker().await?;

            Ok(Self {
                dapr_client: Arc::new(Mutex::new(TodoDaprClient::from_options(opts).await?)),
                workflow_client: Arc::new(Mutex::new(workflow_client)),
                _worker: Arc::new(Mutex::new(worker)),
            })
        }
    }

    pub(crate) async fn store(&self, todo: &Todo) -> anyhow::Result<()> {
        if let Ok(mut dapr_client) = self.dapr_client.try_lock() {
            dapr_client
                .save_state(
                    String::from("statestore"),
                    String::from("todo"),
                    serde_json::to_string(todo)?.into_bytes(),
                    None,
                    None,
                    None,
                )
                .await?;
        }

        Ok(())
    }

    pub(crate) async fn retrieve(&self) -> anyhow::Result<Todo> {
        if let Ok(mut dapr_client) = self.dapr_client.try_lock() {
            let res = dapr_client
                .get_state(String::from("statestore"), String::from("todo"), None)
                .await?;

            let todo: Todo = serde_json::from_slice(res.data.as_slice())?;

            return Ok(todo);
        }

        bail!("Locking the client failed!");
    }

    pub(crate) async fn delete(&self) -> anyhow::Result<()> {
        if let Ok(mut dapr_client) = self.dapr_client.try_lock() {
            dapr_client
                .delete_state(String::from("statestore"), String::from("todo"), None)
                .await?
        }

        bail!("Locking the client failed!");
    }

    pub(crate) async fn schedule(&self) -> anyhow::Result<String> {
        if let Ok(mut workflow_client) = self.workflow_client.try_lock() {
            let instance_id = workflow_client
                .schedule_workflow(
                    workflow::TEST_WORKFLOW,
                    ScheduleOptions::new().with_input(1),
                )
                .await?;

            println!("instance_id={}", instance_id);

            return Ok(instance_id);
        }

        bail!("Locking the client failed!");
    }
}
