//!
//! @package Showcase-Workflow-Engines
//!
//! @file Main entry
//! @copyright 2026-present Christoph Kappel <christoph@unexist.dev>
//! @version $Id$
//!
//! This program can be distributed under the terms of the GNU GPLv2.
//! See the file LICENSE for details.
//!
mod workflow;

mod api;
mod client;
mod database;
mod todo;

use topcoat::{
    Result,
    context::{Cx, context},
    router::{
        Body, Next, Router, RouterBuilderDiscoverExt, Slot, layer, layout, page, response::Response,
    },
    view::{View, view},
};

use crate::{client::TodoClient, database::TodoDatabase};

fn db(cx: &Cx) -> &TodoDatabase {
    context(cx)
}

fn client(cx: &Cx) -> &TodoClient {
    context(cx)
}

#[layout("/")]
async fn root_layout(cx: &Cx, slot: Slot<'_>) -> Result<impl View> {
    Ok(view! {
        <!DOCTYPE html>
        <html>
            <body>
                <nav>
                    <a href="/">"Home"</a>
                    <a href="/todos">"Todos"</a>
                    <a href="/workflows">"Workflows"</a>
                </nav>
                (slot)
            </body>
        </html>
    })
}

#[layer("/api")]
async fn api_log(cx: &Cx, body: Body, next: Next<'_>) -> Result<Response> {
    let response = next.run(cx, body).await?;

    println!("API response: {}", response.status());

    Ok(response)
}

#[page("/")]
async fn home(cx: &Cx) -> Result<impl View> {
    Ok(view! { <h1>"Welcome"</h1> })
}

#[page("/todos")]
async fn todos_list(cx: &Cx) -> Result<impl View> {
    Ok(view! { <h1>"All todos"</h1> })
}

#[page("/workflows")]
async fn workflows_list(cx: &Cx) -> Result<impl View> {
    Ok(view! { <h1>"All workflows"</h1> })
}

#[tokio::main]
async fn main() -> anyhow::Result<()> {
    let router = Router::builder()
        .discover()
        .with(TodoDatabase::default())
        .with(TodoClient::create("http://127.0.0.1:3500").await?)
        .build();

    topcoat::start(router).await?;

    Ok(())
}
