Rails.application.routes.draw do
  resources :accounts #TODO: Remove this!
  get 'pull', to: 'accounts#pull'

  namespace :api do
    namespace :v1 do
      get 'search', to: 'accounts#search'
    end
  end
end
