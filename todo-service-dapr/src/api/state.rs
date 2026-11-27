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

use crate::{client, todo::Todo};

#[route(POST "/api/state")]
async fn state_store(cx: &Cx, Json(todo): Json<Todo>) -> Result<Json<Todo>> {
    match client(cx).store(&todo).await {
        Ok(_) => Ok(Json(todo)),
        Err(err) => {
            println!("Err={}", err);
            Err(topcoat::Error::from_anyhow(err))
        }
    }
}

#[route(GET "/api/state")]
async fn state_retrieve(cx: &Cx) -> Result<Json<Todo>> {
    match client(cx).retrieve().await {
        Ok(todo) => Ok(Json(todo)),
        Err(err) => {
            println!("Err={}", err);
            Err(topcoat::Error::from_anyhow(err))
        }
    }
}

#[route(DELETE "/api/state")]
async fn state_delete(cx: &Cx) -> Result<()> {
    match client(cx).delete().await {
        Ok(ok) => Ok(ok),
        Err(err) => {
            println!("Err={}", err);
            Err(topcoat::Error::from_anyhow(err))
        }
    }
}
