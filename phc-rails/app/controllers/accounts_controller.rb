class AccountsController < ApplicationController
  include Databasedotcom::Rails::Controller

  def index
    @accounts = Account.all
  end

  def show
    @account = Account.find(params[:id])
  end
end
