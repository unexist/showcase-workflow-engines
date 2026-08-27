///
/// @package Showcase-Microservices-Rust
///
/// @file Main entry
/// @copyright 2026-present Christoph Kappel <christoph@unexist.dev>
/// @version $Id$
///
/// This program can be distributed under the terms of the GNU GPLv2.
/// See the file LICENSE for details.
///

use topcoat::{
    Result, context::{Cx, app_context}, router::{Body, Next, Router, RouterBuilderDiscoverExt, content::Json, layer, layout, page, response::Response, route}, view::view,
};
use std::sync::Arc;
use tokio::sync::Mutex;
use dapr::{client::ApiTokenInterceptor, dapr::proto::runtime::v1::dapr_client::DaprClient};
use tonic::{service::interceptor::InterceptedService, transport::Channel};
use anyhow::{bail, anyhow};

#[derive(serde::Deserialize, serde::Serialize, Clone)]
struct Todo {
    title: String,
    description: String,
    done: bool,
}

#[derive(Default)]
struct TodoDatabase {
    list: Arc<Mutex<Vec<Todo>>>,
}

impl TodoDatabase {
    fn push(&self, todo: &Todo) {
        if let Ok(mut list) = self.list.try_lock() {
            list.push(todo.clone());
        }
    }

    async fn get_all(&self) -> Result<Vec<Todo>> {
        Ok(self.list.lock().await.clone())
    }
}

type TodoDaprClient = dapr::Client<DaprClient<InterceptedService<Channel, ApiTokenInterceptor>>>;

struct TodoClient {
    client: Arc<Mutex<TodoDaprClient>>,
}

impl TodoClient {
    fn create(uri: &str) -> impl Future<Output=anyhow::Result<Self>> {
        async {
            match dapr::Client::connect_with_address(uri.to_string()).await {
                Ok(client) => Ok(Self {
                    client: Arc::new(Mutex::new(client)),
                }),
                Err(err) => bail!("That has failed: {}", err)
            }
        }
    }

    async fn store(&self, todo: &Todo) -> anyhow::Result<()> {
        if let Ok(mut client) = self.client.try_lock() {
            client.save_state("todo-service", "todo",
                serde_json::to_string(todo)?.into_bytes(), None, None, None).await?;
        }

        Ok(())
    }

    fn retrieve(&self) -> Result<Todo> {
        todo!()
    }
}

fn db(cx: &Cx) -> &TodoDatabase {
    app_context::<TodoDatabase>(cx)
}

fn client(cx: &Cx) -> &TodoClient {
    app_context::<TodoClient>(cx)
}

#[layout("/")]
async fn root_layout(slot: Result) -> Result {
    view! {
        <!DOCTYPE html>
        <html>
            <body>
                <nav>
                    <a href="/">"Home"</a>
                    <a href="/todos">"Todos"</a>
                </nav>
                (slot?)
            </body>
        </html>
    }
}

#[layer("/api")]
async fn api_log(cx: &Cx, body: Body, next: Next<'_>) -> Result<Response> {
    let response = next.run(cx, body).await?;

    println!("API response: {}", response.status());

    Ok(response)
}

#[page("/")]
async fn home() -> Result {
    view! { <h1>"Welcome"</h1> }
}

#[page("/todos")]
async fn todos_list() -> Result {
    view! { <h1>"All todos"</h1> }
}

#[route(POST "/api/todos")]
async fn create_todo(cx: &Cx, Json(todo): Json<Todo>) -> Result<Json<Todo>> {
    db(cx).push(&todo);

    Ok(Json(todo))
}

#[route(GET "/api/todos")]
async fn get_all(cx: &Cx) -> Result<Json<Vec<Todo>>> {
    Ok(Json(db(cx).get_all().await?))
}

#[route(POST "/api/state/store")]
async fn store_todo(cx: &Cx, Json(todo): Json<Todo>) -> Result<Json<Todo>> {
    if let Err(err) = client(cx).store(&todo).await {
        println!("Err={}", err);
    }

    Ok(Json(todo))
}

#[route(POST "/api/state/retrieve")]
async fn retrieve_todo(cx: &Cx) -> Result<Json<Todo>> {
    match client(cx).retrieve() {
        Ok(todo) => Ok(Json(todo)),
        Err(err) => Err(err),
    }
}

#[tokio::main]
async fn main() -> anyhow::Result<()> {
    let router = Router::builder()
        .discover()
        .app_context(TodoDatabase::default())
        .app_context(TodoClient::create("http://localhost:3500").await?)
        .build();

    topcoat::start(router).await?;

    Ok(())
}
