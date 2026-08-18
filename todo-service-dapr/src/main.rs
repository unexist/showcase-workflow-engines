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
    Result, context::{Cx, CxBuilder, app_context}, router::{Body, Next, Response, Router, RouterBuilderDiscoverExt, content::Json, layer, layout, page, route}, view::view,
};
use std::{mem::MaybeUninit, sync::Arc};
use tokio::sync::Mutex;
use dapr::{client::ApiTokenInterceptor, dapr::proto::runtime::v1::dapr_client::DaprClient};
use tonic::{service::interceptor::InterceptedService, transport::Channel};

#[derive(serde::Deserialize, serde::Serialize, Clone)]
struct Todo {
    title: String,
    description: String,
    done: bool,
}

#[derive(Default)]
struct TodoDatabase {
    todos: Arc<Mutex<Vec<Todo>>>,
}

impl TodoDatabase {
    fn push(&self, todo: &Todo) {
        if let Ok(mut todos) = self.todos.try_lock() {
            todos.push(todo.clone());
        }
    }

    async fn get_all(&self) -> Result<Vec<Todo>> {
        Ok(self.todos.lock().await.clone())
    }
}

type TodoDaprClient = dapr::Client<DaprClient<InterceptedService<Channel, ApiTokenInterceptor>>>;

struct TodoClient {
    client: Arc<MaybeUninit<Mutex<TodoDaprClient>>>,
}

impl Default for TodoClient {
    fn default() -> Self {
        Self {
            client: Arc::<Mutex<TodoDaprClient>>::new_uninit(),
        }
    }
}

impl TodoClient {
    fn store(&self) {
        todo!();
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
async fn api_log(cx: &mut CxBuilder, body: Body, next: Next<'_>) -> Result<Response> {
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
    client(cx).store();

    Ok(Json(todo))
}

#[route(GET "/api/todos")]
async fn get_all(cx: &Cx) -> Result<Json<Vec<Todo>>> {
    Ok(Json(db(cx).get_all().await?))
}

#[tokio::main]
async fn main() -> anyhow::Result<()> {
    let dapr_client = dapr::Client::connect_with_address(
        "http://localhost:3500".to_string()).await?;

    let mut todo_client = TodoClient::default();

    Arc::get_mut(&mut todo_client.client).unwrap().write(Mutex::new(dapr_client));

    let router = Router::builder()
        .discover()
        .app_context(TodoDatabase::default())
        .app_context(todo_client)
        .build();

    topcoat::start(router).await?;

    Ok(())
}
