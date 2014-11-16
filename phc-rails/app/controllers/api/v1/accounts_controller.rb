class Api::V1::AccountsController < ApplicationController
  respond_to :json

  def search
    if request.headers["AuthToken"].eql? ENV["AuthToken"]
      first_name = request.headers["FirstName"]
      last_name = request.headers["LastName"]
      fhash = { first_name: first_name }
      lhash = { last_name: last_name }
      res = PersonAccount.fuzzy_search(fhash, false).fuzzy_search(lhash, false)
      respond_with res
    else
      respond_with []
    end
  end

  def show
    respond_with PersonAccount.find(params[:id])
  end
end
