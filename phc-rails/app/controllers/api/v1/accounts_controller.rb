class Api::V1::AccountsController < ApplicationController
  respond_to :json

  def search
    if request.headers["AuthToken"].eql? ENV["AuthToken"]
      first_name = request.headers["FirstName"]
      last_name = request.headers["LastName"]
      hash = { first_name: first_name, last_name: last_name }
      respond_with PersonAccount.fuzzy_search(hash, false)
    else
      respond_with []
    end
  end

  def show
    respond_with PersonAccount.find(params[:id])
  end
end
