//!
//! @package Showcase-Workflow-Engines
//!
//! @file State API handlers
//! @copyright 2026-present Christoph Kappel <christoph@unexist.dev>
//! @version $Id$
//!
//! This program can be distributed under the terms of the GNU GPLv2.
//! See the file LICENSE for details.
//!

use topcoat::{
    Result,
    context::Cx,
    router::{content::Json, route},
};

use crate::{client, db, todo::Todo};

#[route(POST "/api/todos")]
async fn create_todo(cx: &Cx, Json(todo): Json<Todo>) -> Result<Json<Todo>> {
    db(cx).push(&todo);

    Ok(Json(todo))
}

#[route(GET "/api/todos")]
async fn get_all(cx: &Cx) -> Result<Json<Vec<Todo>>> {
    Ok(Json(db(cx).get_all().await?))
}

#[derive(Default, serde::Serialize, Clone)]
struct WorkflowReply {
    id: String,
}

#[route(POST "/api/workflow")]
async fn workflow_schedule(cx: &Cx) -> Result<Json<WorkflowReply>> {
    match client(cx).schedule().await {
        Ok(instance_id) => Ok(Json(WorkflowReply { id: instance_id })),
        Err(err) => {
            println!("Err={}", err);
            Err(topcoat::Error::from_anyhow(err))
        }
    }
}

mod instance_id {
    use topcoat::{
        Result,
        context::Cx,
        router::{content::Json, path_param, route},
    };

    use crate::client;

    path_param!(instance_id: String else bad_request);

    #[derive(Default, serde::Serialize, Clone)]
    struct WorkflowStatus {
        id: String,
        status: String,
    }

    #[route(GET "/api/workflow/{instance_id}")]
    pub(crate) async fn workflow_metadata(cx: &Cx) -> Result<Json<WorkflowStatus>> {
        let id = instance_id(cx)?.clone();

        match client(cx).metadata(&id).await {
            Ok(status) => Ok(Json(WorkflowStatus { id, status })),
            Err(err) => {
                println!("Err={}", err);
                Err(topcoat::Error::from_anyhow(err))
            }
        }
    }

    #[route(PATCH "/api/workflow/{instance_id}")]
    pub(crate) async fn workflow_terminate(cx: &Cx) -> Result<()> {
        let id = instance_id(cx)?.clone();

        match client(cx).terminate(&id).await {
            Ok(ok) => Ok(ok),
            Err(err) => {
                println!("Err={}", err);
                Err(topcoat::Error::from_anyhow(err))
            }
        }
    }

    #[route(DELETE "/api/workflow/{instance_id}")]
    pub(crate) async fn workflow_purge(cx: &Cx) -> Result<()> {
        let id = instance_id(cx)?.clone();

        match client(cx).purge(&id).await {
            Ok(ok) => Ok(ok),
            Err(err) => {
                println!("Err={}", err);
                Err(topcoat::Error::from_anyhow(err))
            }
        }
    }
}
